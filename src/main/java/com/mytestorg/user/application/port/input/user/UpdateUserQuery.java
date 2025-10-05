package com.mytestorg.user.application.port.input.user;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record UpdateUserQuery(UUID id, String firstName, String lastName, String nickname, String password, String email, String country, OffsetDateTime createdAt, OffsetDateTime updatedAt){
}
