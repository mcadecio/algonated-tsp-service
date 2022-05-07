package com.dercio.algonated_tsp_service.verticles.tsp;

import com.dercio.algonated_tsp_service.response.Response;
import com.dercio.algonated_tsp_service.verticles.ConsumerVerticle;
import com.dercio.algonated_tsp_service.verticles.VerticleAddresses;
import com.dercio.algonated_tsp_service.verticles.runner.code.CodeOptions;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;

import static com.dercio.algonated_tsp_service.verticles.VerticleAddresses.TSP_SUBMISSION;

@Slf4j
public class TSPSubmissionVerticle extends ConsumerVerticle {

    private MessageConsumer<CodeOptions> submissionConsumer;

    @Override
    public void start(Promise<Void> startPromise) {
        var eventBus = vertx.eventBus();
        submissionConsumer = eventBus.consumer(TSP_SUBMISSION.toString());
        submissionConsumer.handler(message -> {
            var request = eventBus.<Response>request(
                    VerticleAddresses.CODE_RUNNER_CONSUMER.toString(),
                    message.body());

            request.onSuccess(reply -> message.reply(reply.body()));
            request.onFailure(error -> message.fail(503, error.getMessage()));
        });

        submissionConsumer.completionHandler(result -> logRegistration(startPromise, result));
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        submissionConsumer.unregister(result -> logUnregistration(stopPromise, result));
    }

    @Override
    public String getAddress() {
        return TSP_SUBMISSION.toString();
    }
}
