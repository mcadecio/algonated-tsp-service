package com.dercio.algonated_tsp_service.runner.demo.algorithms;

import com.dercio.algonated_tsp_service.common.random.UniformRandomGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class StochasticHillClimbing implements Algorithm {

    private final List<List<Integer>> solutions = new ArrayList<>();
    private double delta = 25;

    @Override
    public Solution run(double[][] distances, int iterations) {
        log.info("Running SHC");

        Solution currentSolution = new Solution(distances.length);
        UniformRandomGenerator randomGenerator = new UniformRandomGenerator();
        for (int i = 0; i < iterations; i++) {
            Solution newSolution = currentSolution.copy();
            newSolution.makeSmallChange();

            double newFitness = newSolution.calculateFitness(distances);
            double currentFitness = currentSolution.calculateFitness(distances);

            double acceptanceProbability = acceptanceProbability(newFitness, currentFitness, delta);

            if (randomGenerator.generateDouble(0, 1) > acceptanceProbability) {
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

    public StochasticHillClimbing setDelta(double delta) {
        this.delta = delta;
        return this;
    }

    private double acceptanceProbability(double newFitness, double oldFitness, double stud) {
        double fitnessDifference = oldFitness - newFitness;
        double fitnessExponent = 1 + Math.exp(fitnessDifference / stud);
        return 1.0 / fitnessExponent;
    }
}
