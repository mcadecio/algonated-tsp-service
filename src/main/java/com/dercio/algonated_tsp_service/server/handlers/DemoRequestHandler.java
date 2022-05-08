package com.dercio.algonated_tsp_service.server.handlers;

import com.dercio.algonated_tsp_service.common.response.Response;
import com.dercio.algonated_tsp_service.common.verticle.VerticleAddresses;
import com.dercio.algonated_tsp_service.runner.demo.DemoOptions;
import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

public class DemoRequestHandler implements Handler<RoutingContext> {

    private final Vertx vertx;

    @Inject
    public DemoRequestHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void handle(RoutingContext rc) {
        vertx.eventBus().<Response>request(
                        VerticleAddresses.TSP_DEMO.toString(),
                        rc.getBodyAsJson().mapTo(DemoOptions.class)
                )
                .onSuccess(reply -> {
                    rc.response()
                            .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                            .write(reply.body().encode());
                    rc.next();
                })
                .onFailure(error -> rc.fail(error.getCause()));
    }
}
