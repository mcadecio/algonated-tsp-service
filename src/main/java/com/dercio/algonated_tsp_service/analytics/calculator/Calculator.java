package com.dercio.algonated_tsp_service.analytics.calculator;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public interface Calculator {

    double calculate(double[][] distances, List<Integer> solution);

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface Fitness {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface Efficiency {
    }
}
