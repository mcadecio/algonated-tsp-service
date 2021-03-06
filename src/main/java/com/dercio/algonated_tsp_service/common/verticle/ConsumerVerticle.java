package com.dercio.algonated_tsp_service.common.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ConsumerVerticle extends AbstractVerticle {

    public abstract String getAddress();

    protected void logRegistration(Promise<Void> promise, AsyncResult<Void> result) {
        if (result.succeeded()) {
            log.info("Registered -{}- consumer", getAddress());
        } else {
            log.info("Failed to register -{}- consumer", getAddress());
        }
        promise.complete();
    }

    protected void logUnregistration(Promise<Void> promise, AsyncResult<Void> result) {
        if (result.succeeded()) {
            log.info("Unregistered -{}- consumer", getAddress());
        } else {
            log.info("Failed to unregister -{}- consumer", getAddress());
        }
        promise.complete();
    }
}
