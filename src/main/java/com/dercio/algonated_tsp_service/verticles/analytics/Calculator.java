package com.dercio.algonated_tsp_service.verticles.analytics;

import java.util.List;

public interface Calculator {

    double calculate(double[][] distances, List<Integer> solution);
}
