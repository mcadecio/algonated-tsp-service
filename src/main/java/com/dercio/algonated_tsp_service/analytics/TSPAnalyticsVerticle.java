package com.dercio.algonated_tsp_service.analytics;

import com.dercio.algonated_tsp_service.analytics.calculator.Calculator;
import com.dercio.algonated_tsp_service.common.verticle.ConsumerVerticle;
import com.dercio.algonated_tsp_service.common.verticle.Verticle;
import com.google.inject.Inject;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;

import static com.dercio.algonated_tsp_service.common.verticle.VerticleAddresses.TSP_ANALYTICS_SUMMARY;

@Slf4j
@Verticle
public class TSPAnalyticsVerticle extends ConsumerVerticle {

    private final Calculator efficiencyCalculator;
    private final Calculator fitnessCalculator;

    private MessageConsumer<AnalyticsRequest> consumer;

    @Inject
    public TSPAnalyticsVerticle(
            @Calculator.Efficiency Calculator efficiencyCalculator,
            @Calculator.Fitness Calculator fitnessCalculator) {
        this.efficiencyCalculator = efficiencyCalculator;
        this.fitnessCalculator = fitnessCalculator;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        consumer = vertx.eventBus().consumer(getAddress());
        consumer.handler(message -> {
            log.info("Consuming message");
            var request = message.body();
            message.reply(createSummary(request));
        }).completionHandler(result -> logRegistration(startPromise, result));
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        consumer.unregister(result -> logUnregistration(stopPromise, result));
    }

    @Override
    public String getAddress() {
        return TSP_ANALYTICS_SUMMARY.toString();
    }

    private AnalyticsSummary createSummary(AnalyticsRequest request) {
        var summary = new AnalyticsSummary();
        summary.setIterations(request.getIterations());
        summary.setTimeRun(request.getTimeElapsed());
        summary.setEfficacy(efficiencyCalculator.calculate(request.getDistances(), request.getSolution()));
        summary.setFitness(fitnessCalculator.calculate(request.getDistances(), request.getSolution()));
        return summary;
    }

}
