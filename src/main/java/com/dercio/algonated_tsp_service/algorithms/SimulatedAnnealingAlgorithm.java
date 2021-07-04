package com.dercio.algonated_tsp_service.algorithms;

import com.dercio.algonated_tsp_service.random.UniformRandomGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimulatedAnnealingAlgorithm implements Algorithm {

    private static final Logger logger = LoggerFactory.getLogger(SimulatedAnnealingAlgorithm.class);
    private final UniformRandomGenerator randomGenerator = new UniformRandomGenerator();
    private final List<List<Integer>> solutions = new ArrayList<>();
    private Double optionalTemp;
    private Double optionalCR;

    @Override
    public Solution run(double[][] distances, int iterations) {
        logger.info("Running SA");

        double temperature = Optional.ofNullable(optionalTemp).orElse(calculateTemp(distances));
        double coolingRate = Optional.ofNullable(optionalCR).orElse(calcCR(temperature, iterations));

        Solution finalSolution = new Solution(distances.length);

        for (int i = 0; i < iterations; i++) {
            finalSolution = calculateNewSolution(distances, temperature, finalSolution);
            solutions.add(finalSolution.getSolution());
            temperature = coolingRate * temperature;
        }


        return finalSolution;
    }

    @Override
    public List<List<Integer>> getSolutions() {
        return solutions;
    }

    public SimulatedAnnealingAlgorithm setOptionalTemp(double optionalTemp) {
        this.optionalTemp = optionalTemp;
        return this;
    }

    public SimulatedAnnealingAlgorithm setOptionalCR(double optionalCR) {
        this.optionalCR = optionalCR;
        return this;
    }

    private Solution calculateNewSolution(double[][] distances, double temperature, Solution finalSolution) {
        Solution temporarySolution = finalSolution.copy();
        temporarySolution.makeSmallChange();
        double temporarySolutionFitness = temporarySolution.calculateFitness(distances);

        double finalSolutionFitness = finalSolution.calculateFitness(distances);

        if (temporarySolutionFitness > finalSolutionFitness) {
            double changeProbability = acceptanceFunction(temporarySolutionFitness, finalSolutionFitness, temperature);

            if (changeProbability > randomGenerator.generateDouble(0, 1)) {
                finalSolution = temporarySolution.copy();
            }

        } else {
            finalSolution = temporarySolution.copy();
        }

        return finalSolution;
    }

    private double acceptanceFunction(double newFitness, double oldFitness, double temperature) {
        double delta = Math.abs(oldFitness - newFitness);
        delta = -1 * delta;
        return Math.exp(delta / temperature);
    }

    private double calcCR(double temperature, int nIterations) {
        double tIter = 0.001;
        double power = 1.0 / nIterations;
        double tValue = tIter / temperature;

        return Math.pow(tValue, power);
    }

    private double calculateTemp(double[][] d) {
        double averageDistance = getAverage(d) * d.length;
        averageDistance = averageDistance / 2;
        return averageDistance;
    }

    private double getAverage(double[][] array) {
        int counter = 0;
        double sum = 0;
        for (int i = 1; i < array.length; i++) {
            for (int j = i; j < array.length; j++) {
                sum = sum + array[i][j];
                counter++;
            }
        }

        if (counter == 0) {
            counter++;
        }
        return sum / counter;
    }

}
