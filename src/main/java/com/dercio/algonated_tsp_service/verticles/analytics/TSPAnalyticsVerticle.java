package com.dercio.algonated_tsp_service.verticles.analytics;

import com.dercio.algonated_tsp_service.verticles.ConsumerVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;

import static com.dercio.algonated_tsp_service.verticles.VerticleAddresses.TSP_ANALYTICS_SUMMARY;

@Slf4j
public class TSPAnalyticsVerticle extends ConsumerVerticle {

    private MessageConsumer<AnalyticsRequest> consumer;
    private final Calculator efficiencyCalculator;
    private final Calculator fitnessCalculator;

    public TSPAnalyticsVerticle() {
        this(
                new TSPEfficiencyCalculator(),
                new TSPFitnessCalculator()
        );
    }

    TSPAnalyticsVerticle(
            Calculator efficiencyCalculator,
            Calculator fitnessCalculator) {
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
