package com.dercio.algonated_tsp_service.server.handlers;

import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;

import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

public class HealthHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext rc) {
        var reply = new HashMap<>();
        var runtime = Runtime.getRuntime();
        reply.put("status", "up");
        reply.put("freeMemory", runtime.freeMemory());
        reply.put("maxMemory", runtime.maxMemory());
        reply.put("totalMemory", runtime.totalMemory());
        rc.response()
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(Json.encodePrettily(reply));

    }
}
