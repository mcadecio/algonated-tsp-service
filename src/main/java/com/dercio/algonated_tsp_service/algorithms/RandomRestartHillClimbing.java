package com.dercio.algonated_tsp_service.algorithms;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RandomRestartHillClimbing extends RandomHillClimbingAlgorithm {

    private int restarts = 10;

    @Override
    public Solution run(double[][] distances, int iterations) {
        log.info("Running RRHC");

        Solution finalSolution = new Solution(distances.length);

        for (int i = 0; i < restarts; i++) {
            Solution newSolution = super.run(distances, iterations / restarts);

            if (newSolution.calculateFitness(distances) < finalSolution.calculateFitness(distances)) {
                finalSolution = newSolution.copy();
            }
        }

        return finalSolution;
    }

    public RandomHillClimbingAlgorithm setRestarts(int restarts) {
        this.restarts = restarts;
        return this;
    }

}
