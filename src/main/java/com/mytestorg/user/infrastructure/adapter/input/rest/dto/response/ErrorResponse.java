/*
 * Copyright (c) KLM Royal Dutch Airlines. All Rights Reserved.
 * ============================================================
 */

package com.mytestorg.user.infrastructure.adapter.input.rest.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.util.Map;

public record ErrorResponse(
                @NotNull
                @NonNull
                ErrorCode code,

                @NotBlank
                @NonNull
                String message,
                Map<String, String> fieldErrors

) {

    public enum ErrorCode {
        REQ_INVALID,
        REQ_RESOURCE_NOT_FOUND,
        USER_ALREADY_EXISTS,
        SERVER_ERROR
    }
}
