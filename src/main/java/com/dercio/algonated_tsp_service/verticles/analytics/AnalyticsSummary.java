package com.dercio.algonated_tsp_service.verticles.analytics;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class AnalyticsSummary {
    private int iterations;
    private double timeRun;
    private double fitness;
    private double efficacy;
}
