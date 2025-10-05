package com.mytestorg.user.infrastructure.adapter.input.rest.dto.response;

import com.mytestorg.user.infrastructure.configuration.serializer.JavaOffsetDateTimeWithOffsetSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.OffsetDateTime;

public record UserResponse(String id, String firstName, String lastName, String nickname, String email, String country,
                           @JsonSerialize(using = JavaOffsetDateTimeWithOffsetSerializer.class) OffsetDateTime createdAt,
                           @JsonSerialize(using = JavaOffsetDateTimeWithOffsetSerializer.class) OffsetDateTime updatedAt) {
}
