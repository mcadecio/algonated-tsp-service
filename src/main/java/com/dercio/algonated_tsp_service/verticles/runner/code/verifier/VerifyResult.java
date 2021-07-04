package com.dercio.algonated_tsp_service.verticles.runner.code.verifier;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VerifyResult {
    private final boolean isSuccess;
    private final String errorMessage;
}
