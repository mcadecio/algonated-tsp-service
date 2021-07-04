package com.dercio.algonated_tsp_service.verticles.analytics;


import com.dercio.algonated_tsp_service.algorithms.Solution;

import java.util.List;

public class TSPFitnessCalculator implements Calculator {

    @Override
    public double calculate(double[][] distances, List<Integer> solution) {
        return new Solution(solution).calculateFitness(distances);
    }
}
