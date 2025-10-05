package com.mytestorg.user.application.port.input.user;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record CreateUserQuery(String id, String firstName, String lastName, String nickname, String password,
                              String email, String country, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
}
