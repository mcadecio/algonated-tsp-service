package com.dercio.algonated_tsp_service.common;

@FunctionalInterface
public interface AnnotationProcessor<T> {

    void process(T argument);
    
    default String basePackage() {
        return System.getProperty("project.base.package");
    }
}
