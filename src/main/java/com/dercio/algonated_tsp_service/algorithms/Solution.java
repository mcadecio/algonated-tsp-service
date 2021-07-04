package com.dercio.algonated_tsp_service.algorithms;

import com.dercio.algonated_tsp_service.random.UniformRandomGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Solution {
    private static final UniformRandomGenerator randomGenerator = new UniformRandomGenerator();
    private final List<Integer> rawSolution;

    public Solution(int length) {
        this(generateRandomList(length));
    }

    public Solution(List<Integer> rawSolution) {
        this.rawSolution = rawSolution;
    }


    public double calculateFitness(double[][] distances) {
        if (rawSolution.size() != distances.length) return (-1);

        int numberOfCities = rawSolution.size();

        double sum = 0;

        for (int i = 0; i < numberOfCities - 1; i++) {
            int city = rawSolution.get(i);
            int nextCity = rawSolution.get(i + 1);
            sum = sum + distances[city][nextCity];
        }

        int endCity = rawSolution.get(rawSolution.size() - 1);
        int startCity = rawSolution.get(0);

        sum = sum + distances[endCity][startCity];

        return sum;
    }

    public void makeSmallChange() {
        int firstCity = 0;
        int secondCity = 0;

        while (firstCity == secondCity) {
            firstCity = randomGenerator.generateInteger(0, rawSolution.size() - 1);
            secondCity = randomGenerator.generateInteger(0, rawSolution.size() - 1);
        }

        Collections.swap(rawSolution, firstCity, secondCity);
    }

    public List<Integer> getSolution() {
        return rawSolution;
    }

    public Solution copy() {
        return new Solution(new ArrayList<>(rawSolution));
    }

    private static List<Integer> generateRandomList(int size) {
        List<Integer> orderedList = createZeroList(size);
        List<Integer> shuffledList = new ArrayList<>();

        while (!orderedList.isEmpty()) {
            int randomIndex = randomGenerator.generateInteger(0, orderedList.size() - 1);
            shuffledList.add(orderedList.get(randomIndex));
            orderedList.remove(randomIndex);
        }

        return shuffledList;
    }

    private static List<Integer> createZeroList(int size) {
        List<Integer> zeroList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            zeroList.add(i);
        }

        return zeroList;
    }
}
