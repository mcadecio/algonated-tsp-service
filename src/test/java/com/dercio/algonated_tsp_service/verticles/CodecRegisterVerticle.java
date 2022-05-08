package com.dercio.algonated_tsp_service.verticles;

import com.dercio.algonated_tsp_service.analytics.AnalyticsRequest;
import com.dercio.algonated_tsp_service.analytics.AnalyticsSummary;
import com.dercio.algonated_tsp_service.common.codec.GenericCodec;
import com.dercio.algonated_tsp_service.common.response.Response;
import com.dercio.algonated_tsp_service.runner.code.CodeOptions;
import com.dercio.algonated_tsp_service.runner.demo.DemoOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CodecRegisterVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        vertx.eventBus()
                .registerDefaultCodec(
                        AnalyticsRequest.class,
                        new GenericCodec<>(AnalyticsRequest.class))
                .registerDefaultCodec(
                        AnalyticsSummary.class,
                        new GenericCodec<>(AnalyticsSummary.class))
                .registerDefaultCodec(
                        CodeOptions.class,
                        new GenericCodec<>(CodeOptions.class))
                .registerDefaultCodec(
                        Response.class,
                        new GenericCodec<>(Response.class))
                .registerDefaultCodec(
                        DemoOptions.class,
                        new GenericCodec<>(DemoOptions.class));
        startPromise.complete();
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        vertx.eventBus()
                .unregisterDefaultCodec(AnalyticsRequest.class)
                .unregisterDefaultCodec(AnalyticsSummary.class)
                .unregisterDefaultCodec(CodeOptions.class)
                .unregisterDefaultCodec(DemoOptions.class)
                .unregisterDefaultCodec(Response.class);
        stopPromise.complete();
    }
}
