package com.mytestorg.user.infrastructure.adapter.input.rest.dto.request;

import com.mytestorg.user.application.port.input.user.UpdateUserQuery;
import com.mytestorg.user.infrastructure.adapter.input.rest.validator.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record UpdateUserRequest(@NotNull @NotEmpty String firstName, @NotNull @NotEmpty String lastName, String nickname, @ValidPassword String password, @NotNull @NotEmpty @Email(message = "Please provide a valid email address") String email, @NotNull @NotEmpty String country) {
    public UpdateUserQuery toUpdateUserQuery() {
        return UpdateUserQuery.builder()
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
