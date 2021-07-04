package com.dercio.algonated_tsp_service.verticles.runner.code;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CodeOptions {
    private String className;
    private String packageName;
    private String methodToCall;
    private int iterations;
    private List<String> importsAllowed;
    private List<String> illegalMethods;
    @JsonAlias({"data", "distances"})
    private double[][] distances;
    private String code;
}
