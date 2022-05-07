package com.dercio.algonated_tsp_service.verticles.analytics;

import com.dercio.algonated_tsp_service.verticles.VerticleAddresses;
import com.dercio.algonated_tsp_service.verticles.codec.CodecRegisterVerticle;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class TSPAnalyticsVerticleTest {

    private static final Vertx vertx = Vertx.vertx();

    @BeforeAll
    public static void prepare(VertxTestContext testContext) {
        vertx.deployVerticle(new CodecRegisterVerticle());
        vertx.deployVerticle(
                new TSPAnalyticsVerticle(
                        (data, solution) -> 6.0,
                        (data, solution) -> 2.0
                ), testContext.succeeding(id -> testContext.completeNow())
        );
    }

    @Test
    @DisplayName("Returns a summary for the given request")
    void createSummaryForRequest(VertxTestContext testContext) {
        double[][] distance = {
                {1, 2, 3},
                {2, 1, 3},
                {3, 2, 1}
        };
        var request = AnalyticsRequest.builder()
                .iterations(1)
                .timeElapsed(20)
                .distances(distance)
                .solution(List.of(1, 0, 1))
                .build();

        vertx.eventBus().<AnalyticsSummary>request(
                VerticleAddresses.TSP_ANALYTICS_SUMMARY.toString(),
                request,
                messageReply -> testContext.verify(() -> {
                    var summary = messageReply.result().body();

                    assertEquals(request.getIterations(), summary.getIterations());
                    assertEquals(request.getTimeElapsed(), summary.getTimeRun());
                    assertEquals(6.0, summary.getEfficacy());
                    assertEquals(2.0, summary.getFitness());

                    testContext.completeNow();
                })
        );

    }

    @AfterAll
    public static void cleanup() {
        vertx.close();
    }
}