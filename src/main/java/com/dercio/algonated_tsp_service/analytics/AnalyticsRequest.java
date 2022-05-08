package com.dercio.algonated_tsp_service.analytics;

import com.dercio.algonated_tsp_service.common.codec.Codec;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Codec
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
