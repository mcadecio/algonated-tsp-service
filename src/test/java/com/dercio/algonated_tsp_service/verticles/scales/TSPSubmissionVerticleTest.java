package com.dercio.algonated_tsp_service.verticles.scales;

import com.dercio.algonated_tsp_service.common.response.Response;
import com.dercio.algonated_tsp_service.common.verticle.VerticleAddresses;
import com.dercio.algonated_tsp_service.runner.code.CodeOptions;
import com.dercio.algonated_tsp_service.runner.code.CodeRunnerVerticle;
import com.dercio.algonated_tsp_service.submission.TSPSubmissionVerticle;
import com.dercio.algonated_tsp_service.verticles.CodecRegisterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class TSPSubmissionVerticleTest {
    private static final Vertx vertx = Vertx.vertx();

    private static Function<CodeOptions, Response> mockedBehavior;

    @BeforeAll
    public static void prepare(VertxTestContext testContext) {
        vertx.deployVerticle(new CodecRegisterVerticle());
        vertx.deployVerticle(new CodeRunnerVerticle() {
            @Override
            public void start(Promise<Void> startPromise) {
                vertx.eventBus()
                        .<CodeOptions>consumer(getAddress())
                        .handler(message ->
                                message.reply(mockedBehavior.apply(message.body())));
            }

            @Override
            public void stop(Promise<Void> stopPromise) {
                // do nothing
            }

        });
        vertx.deployVerticle(
                new TSPSubmissionVerticle(),
                testContext.succeeding(id -> testContext.completeNow())
        );
    }

    @Test
    @DisplayName("Returns a successful response")
    void returnSuccessfulResponse(VertxTestContext testContext) {
        var request = CodeOptions.builder()
                .className("test-class-name")
                .build();

        mockedBehavior = options -> new Response()
                .setSuccess(true)
                .setConsoleOutput(options.getClassName());

        vertx.eventBus().<Response>request(
                VerticleAddresses.TSP_SUBMISSION.toString(),
                request,
                messageReply -> testContext.verify(() -> {
                    var response = messageReply.result().body();
                    assertEquals(request.getClassName(), response.getConsoleOutput());
                    assertTrue(response.isSuccess());
                    testContext.completeNow();
                })
        );

    }

    @Test
    @DisplayName("Returns a unsuccessful response")
    void returnUnsuccessfulResponse(VertxTestContext testContext) {
        var request = CodeOptions.builder()
                .className("test-class-name")
                .build();

        mockedBehavior = options -> new Response()
                .setSuccess(false)
                .setConsoleOutput("Error");

        vertx.eventBus().<Response>request(
                VerticleAddresses.TSP_SUBMISSION.toString(),
                request,
                messageReply -> testContext.verify(() -> {
                    var response = messageReply.result().body();
                    assertFalse(response.isSuccess());
                    assertEquals("Error", response.getConsoleOutput());
                    testContext.completeNow();
                })
        );

    }

    @AfterAll
    public static void cleanup() {
        vertx.close();
    }

}