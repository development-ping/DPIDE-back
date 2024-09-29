package com.dpide.dpide.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SuccessResponse {
    private final int status;
    private final String message;
}
