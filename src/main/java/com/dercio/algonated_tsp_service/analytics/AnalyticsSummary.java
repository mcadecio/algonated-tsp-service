package com.dercio.algonated_tsp_service.analytics;

import com.dercio.algonated_tsp_service.common.codec.Codec;
import lombok.*;

@Codec
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
