package com.dercio.algonated_tsp_service.analytics;

import com.dercio.algonated_tsp_service.analytics.calculator.Calculator;
import com.dercio.algonated_tsp_service.analytics.calculator.TSPEfficiencyCalculator;
import com.dercio.algonated_tsp_service.analytics.calculator.TSPFitnessCalculator;
import com.dercio.algonated_tsp_service.common.module.Module;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import static com.dercio.algonated_tsp_service.analytics.calculator.Calculator.Efficiency;
import static com.dercio.algonated_tsp_service.analytics.calculator.Calculator.Fitness;

@Module
public class AnalyticsModule extends AbstractModule {

    @Provides
    @Efficiency
    public Calculator efficiencyCalculator() {
        return new TSPEfficiencyCalculator();
    }

    @Provides
    @Fitness
    public Calculator fitnessCalculator() {
        return new TSPFitnessCalculator();
    }
}
