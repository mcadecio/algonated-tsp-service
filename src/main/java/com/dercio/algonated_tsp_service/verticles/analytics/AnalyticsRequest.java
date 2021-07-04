package com.dercio.algonated_tsp_service.verticles.analytics;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class AnalyticsRequest {
    private final long timeElapsed;
    private final int iterations;
    private final double[][] distances;
    private final List<Integer> solution;
}
