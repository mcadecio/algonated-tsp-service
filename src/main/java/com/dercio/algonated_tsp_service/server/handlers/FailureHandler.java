package com.dercio.algonated_tsp_service.server.handlers;

import com.dercio.algonated_tsp_service.common.response.Response;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@Slf4j
public class FailureHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext event) {

        log.error("Error: {}", event.failure().getMessage());
        event.response()
                .setStatusCode(400)
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(new Response()
                        .setSuccess(false)
                        .setConsoleOutput(extractMessage(event.failure()))
                        .encode());
    }

    private String extractMessage(Throwable throwable) {
        if (throwable instanceof NullPointerException) {
            return "Internal Server Error: NullPointerException";
        } else {
            return throwable.getMessage();
        }
    }
}
