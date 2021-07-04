package com.dercio.algonated_tsp_service.verticles.analytics;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TSPEfficiencyCalculator implements Calculator {

    @Override
    public double calculate(double[][] distances, List<Integer> candidateSolution) {
        String optFilename = "./TSP_DATA/TSP_" + distances.length + "_OPT.txt";

        List<Integer> rawOptimalSolution;
        try (Stream<String> lines = Files.lines(Paths.get(optFilename))) {
            rawOptimalSolution = lines
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return -1;
        }

        double candidateFitness = new TSPFitnessCalculator().calculate(distances, candidateSolution);
        double optimalFitness = new TSPFitnessCalculator().calculate(distances, rawOptimalSolution);

        return (optimalFitness / candidateFitness) * 100;
    }
}
