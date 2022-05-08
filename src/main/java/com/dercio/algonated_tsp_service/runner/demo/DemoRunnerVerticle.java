package com.dercio.algonated_tsp_service.runner.demo;

import com.dercio.algonated_tsp_service.analytics.AnalyticsRequest;
import com.dercio.algonated_tsp_service.analytics.AnalyticsSummary;
import com.dercio.algonated_tsp_service.common.response.Response;
import com.dercio.algonated_tsp_service.common.verticle.ConsumerVerticle;
import com.dercio.algonated_tsp_service.common.verticle.Verticle;
import com.dercio.algonated_tsp_service.runner.demo.algorithms.Algorithm;
import com.google.common.base.Stopwatch;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static com.dercio.algonated_tsp_service.common.verticle.VerticleAddresses.DEMO_RUNNER_CONSUMER;
import static com.dercio.algonated_tsp_service.common.verticle.VerticleAddresses.TSP_ANALYTICS_SUMMARY;

@Slf4j
@Verticle
public class DemoRunnerVerticle extends ConsumerVerticle {

    private MessageConsumer<DemoOptions> consumer;

    @Override
    public void start(Promise<Void> startPromise) {
        var eventBus = vertx.eventBus();
        consumer = eventBus.consumer(getAddress());
        consumer.handler(this::handleMessage);
        consumer.completionHandler(result -> logRegistration(startPromise, result));
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        consumer.unregister(result -> logUnregistration(stopPromise, result));
    }

    @Override
    public String getAddress() {
        return DEMO_RUNNER_CONSUMER.toString();
    }

    private void handleMessage(Message<DemoOptions> message) {
        var request = message.body();
        Algorithm algorithm = Algorithm.getTSPAlgorithm(request);

        Stopwatch timer = Stopwatch.createStarted();
        var solution = algorithm.run(request.getDistances(), request.getIterations())
                .getSolution();
        timer.stop();

        var analyticsRequest = AnalyticsRequest.builder()
                .solution(solution)
                .iterations(request.getIterations())
                .timeElapsed(timer.elapsed(TimeUnit.MILLISECONDS))
                .distances(request.getDistances())
                .build();


        var analyticsSummary = vertx.eventBus()
                .<AnalyticsSummary>request(TSP_ANALYTICS_SUMMARY.toString(), analyticsRequest);

        analyticsSummary.onSuccess(reply -> {
            var summary = reply.body();
            message.reply(new Response()
                    .setSuccess(true)
                    .setConsoleOutput("Your Demo is ready!")
                    .setResult(solution)
                    .setData(request.getDistances())
                    .setSummary(summary)
                    .setSolutions(algorithm.getSolutions()));
        });

        analyticsSummary.onFailure(error -> {
            log.error("Error: {}", error.getCause().getMessage());
            message.fail(400, error.getCause().getMessage());
        });
    }

}
