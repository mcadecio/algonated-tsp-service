package com.dercio.algonated_tsp_service.verticles.tsp;

import com.dercio.algonated_tsp_service.response.Response;
import com.dercio.algonated_tsp_service.verticles.ConsumerVerticle;
import com.dercio.algonated_tsp_service.verticles.VerticleAddresses;
import com.dercio.algonated_tsp_service.verticles.runner.demo.DemoOptions;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class TSPDemoVerticle extends ConsumerVerticle {

    private MessageConsumer<DemoOptions> consumer;

    @Override
    public void start(Promise<Void> startPromise) {
        var eventBus = vertx.eventBus();
        consumer = eventBus.consumer(getAddress());
        consumer.handler(message -> {
            var demoRequest = eventBus.<Response>request(
                    VerticleAddresses.DEMO_RUNNER_CONSUMER.toString(),
                    message.body()
            );

            demoRequest.onSuccess(reply -> message.reply(reply.body()));
            demoRequest.onFailure(error -> message.fail(503, error.getMessage()));
        });

        consumer.completionHandler(result -> logRegistration(startPromise, result));
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        consumer.unregister(result -> logUnregistration(stopPromise, result));
    }

    @Override
    public String getAddress() {
        return VerticleAddresses.TSP_DEMO.toString();
    }
}
