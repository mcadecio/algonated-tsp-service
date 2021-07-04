package com.dercio.algonated_tsp_service.verticles.runner.demo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoOptions {
    private String algorithm;
    private int iterations;
    private double temperature;
    private double coolingRate;
    private int delta;
    private int restarts;

    @JsonAlias({"distances", "data"})
    private double[][] distances;

}

