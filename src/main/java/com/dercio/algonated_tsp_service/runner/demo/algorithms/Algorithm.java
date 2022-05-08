package com.dercio.algonated_tsp_service.runner.demo.algorithms;

import com.dercio.algonated_tsp_service.runner.demo.DemoOptions;

import java.util.List;

public interface Algorithm {
    static Algorithm getTSPAlgorithm(DemoOptions request) {
        Algorithm algorithm;
        String requestedAlgorithm = request.getAlgorithm();
        switch (requestedAlgorithm) {
            case "sa":
                algorithm = new SimulatedAnnealingAlgorithm()
                        .setOptionalCR(request.getCoolingRate())
                        .setOptionalTemp(request.getTemperature());
                break;
            case "rrhc":
                algorithm = new RandomRestartHillClimbing()
                        .setRestarts(request.getRestarts());
                break;
            case "shc":
                algorithm = new StochasticHillClimbing()
                        .setDelta(request.getDelta());
                break;
            default:
                algorithm = new RandomHillClimbingAlgorithm();
                break;
        }

        return algorithm;
    }

    Solution run(double[][] distances, int iterations);

    List<List<Integer>> getSolutions();
}

