package com.dercio.algonated_tsp_service.runner.demo.algorithms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RandomHillClimbingAlgorithm implements Algorithm {

    private static final Logger logger = LoggerFactory.getLogger(RandomHillClimbingAlgorithm.class);
    private final List<List<Integer>> solutions = new ArrayList<>();

    @Override
    public Solution run(double[][] distances, int iterations) {
        logger.info("Running RMHC");

        Solution currentSolution = new Solution(distances.length);

        for (int i = 0; i < iterations; i++) {
            Solution newSolution = currentSolution.copy();
            newSolution.makeSmallChange();

            double newFitness = newSolution.calculateFitness(distances);
            double currentFitness = currentSolution.calculateFitness(distances);

            if (newFitness < currentFitness) {
                currentSolution = newSolution.copy();
            }

            if (newFitness == 0) {
                currentSolution = newSolution.copy();
                solutions.add(currentSolution.getSolution());
                break;
            }

            solutions.add(currentSolution.getSolution());
        }

        return currentSolution;
    }

    @Override
    public List<List<Integer>> getSolutions() {
        return solutions;
    }
}
