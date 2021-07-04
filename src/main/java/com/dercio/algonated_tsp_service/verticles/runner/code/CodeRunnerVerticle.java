package com.dercio.algonated_tsp_service.verticles.runner.code;

import com.dercio.algonated_tsp_service.response.Response;
import com.dercio.algonated_tsp_service.verticles.ConsumerVerticle;
import com.dercio.algonated_tsp_service.verticles.analytics.AnalyticsRequest;
import com.dercio.algonated_tsp_service.verticles.analytics.CodeRunnerSummary;
import com.dercio.algonated_tsp_service.verticles.runner.code.verifier.IllegalMethodVerifier;
import com.dercio.algonated_tsp_service.verticles.runner.code.verifier.ImportVerifier;
import com.dercio.algonated_tsp_service.verticles.runner.code.verifier.VerifyResult;
import com.google.common.base.Stopwatch;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;
import org.joor.Reflect;
import org.joor.ReflectException;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.dercio.algonated_tsp_service.verticles.VerticleAddresses.CODE_RUNNER_CONSUMER;
import static com.dercio.algonated_tsp_service.verticles.VerticleAddresses.TSP_ANALYTICS_SUMMARY;

@Slf4j
public class CodeRunnerVerticle extends ConsumerVerticle {

    private static final String PLEASE_REMOVE = "Please remove the following ";

    private MessageConsumer<CodeOptions> consumer;

    @Override
    public void start(Promise<Void> startPromise) {
        consumer = vertx.eventBus().consumer(getAddress());
        consumer
                .handler(this::handleMessage)
                .completionHandler(result -> logRegistration(startPromise, result));
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        consumer.unregister(result -> logUnregistration(stopPromise, result));
    }

    @Override
    public String getAddress() {
        return CODE_RUNNER_CONSUMER.toString();
    }

    private void handleMessage(Message<CodeOptions> message) {
        log.info("Consuming message");
        CodeOptions options = message.body();

        var verifyResult = verifyCode(options);
        if (!verifyResult.isSuccess()) {
            message.fail(400, verifyResult.getErrorMessage());
            return;
        }

        var compileResult = compile(options);
        if (!compileResult.isSuccess()) {
            message.fail(400, compileResult.getErrorMessage());
            return;
        }

        var executionResult = execute(options, compileResult.getCompiledClass());
        if (!executionResult.isSuccess()) {
            message.fail(400, executionResult.getErrorMessage());
            return;
        }

        var analyticsRequest = AnalyticsRequest.builder()
                .solution(executionResult.getSolution())
                .iterations(options.getIterations())
                .timeElapsed(executionResult.getTimeElapsed())
                .distances(options.getDistances())
                .build();

        vertx.eventBus().<CodeRunnerSummary>request(
                TSP_ANALYTICS_SUMMARY.toString(),
                analyticsRequest,
                reply -> {
                    if (reply.succeeded()) {
                        var codeRunnerSummary = reply.result().body();
                        message.reply(new Response()
                                .setSuccess(true)
                                .setConsoleOutput(executionResult.getErrorMessage())
                                .setResult(executionResult.getSolution())
                                .setData(options.getDistances())
                                .setSummary(codeRunnerSummary)
                                .setSolutions(executionResult.getSolutions()));
                    } else {
                        log.error("Error: {}", reply.cause().getMessage());
                        message.fail(400, reply.cause().getMessage());
                    }
                }
        );
    }

    public CompileResult compile(CodeOptions options) {
        try {
            var compiledClass = compileClass(options);
            return CompileResult.builder()
                    .compiledClass(compiledClass)
                    .errorMessage("")
                    .isSuccess(true)
                    .build();
        } catch (ReflectException reflectException) {
            return CompileResult.builder()
                    .isSuccess(false)
                    .errorMessage(reflectException.getMessage())
                    .build();
        }
    }

    public ExecutionResult execute(CodeOptions options, Reflect compiledClass) {
        String errorMessage;
        try {
            var timer = Stopwatch.createStarted();
            List<Integer> solution = compiledClass.call(options.getMethodToCall(), options.getDistances(), options.getIterations())
                    .get();
            timer.stop();
            List<List<Integer>> solutions = compiledClass.get("solutions");

            return ExecutionResult.builder()
                    .solution(solution)
                    .solutions(solutions)
                    .isSuccess(true)
                    .errorMessage("Compile and Run was a success")
                    .timeElapsed(timer.elapsed(TimeUnit.MILLISECONDS))
                    .build();
        } catch (Exception exception) {
            log.error("Exception while executing code: ", exception);
            errorMessage = exception.getMessage();
        }

        return ExecutionResult.builder()
                .errorMessage(errorMessage)
                .isSuccess(false)
                .build();
    }

    private VerifyResult verifyCode(CodeOptions options) {
        List<String> importsFound = new ImportVerifier(options.getImportsAllowed())
                .verify(options.getCode());
        if (!importsFound.isEmpty()) {
            return VerifyResult.builder()
                    .isSuccess(false)
                    .errorMessage(PLEASE_REMOVE + "imports:\n" + importsFound)
                    .build();
        }

        List<String> illegalMethods = new IllegalMethodVerifier(options.getIllegalMethods())
                .verify(options.getCode());
        if (!illegalMethods.isEmpty()) {
            return VerifyResult.builder()
                    .isSuccess(false)
                    .errorMessage(PLEASE_REMOVE + "illegal methods:\n" + illegalMethods)
                    .build();
        }

        return VerifyResult.builder()
                .isSuccess(true)
                .build();
    }

    private Reflect compileClass(CodeOptions options) {
        String packageName = "package " + options.getPackageName() + ";";
        String className = options.getPackageName() + "." + options.getClassName();
        return Reflect.compile(className, packageName +
                "\n" +
                String.join("\n", options.getImportsAllowed()) +
                "\n" +
                options.getCode()
        ).create();
    }

}
