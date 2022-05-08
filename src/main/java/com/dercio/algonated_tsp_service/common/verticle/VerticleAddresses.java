package com.dercio.algonated_tsp_service.common.verticle;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum VerticleAddresses {

    TSP_ANALYTICS_SUMMARY("tsp-analytics-summary"),
    CODE_RUNNER_CONSUMER("code-runner-consumer"),
    DEMO_RUNNER_CONSUMER("demo-runner-consumer"),
    TSP_SUBMISSION("tsp-submission-consumer"),
    TSP_DEMO("tsp-demo-consumer");

    private final String address;

    @Override
    public String toString() {
        return address;
    }
}
