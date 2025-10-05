package com.mytestorg.user.infrastructure.adapter.input.rest.dto.request;

import com.mytestorg.user.application.port.input.user.CreateUserQuery;
import com.mytestorg.user.infrastructure.adapter.input.rest.validator.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateUserRequest(@NotNull @NotEmpty String firstName,@NotNull @NotEmpty String lastName, String nickname,
                                @ValidPassword String password, @Email(message = "Please provide a valid email address")
@NotEmpty @NotNull String email, @NotNull @NotEmpty String country) {
    public CreateUserQuery toCreateUserQuery() {
        return CreateUserQuery.builder()
                .id(UUID.randomUUID().toString())
                .firstName(firstName)
                .lastName(lastName)
                .nickname(nickname)
                .password(password)
                .email(email)
                .country(country)
                .createdAt(OffsetDateTime.now())
                .build();
    }
}
