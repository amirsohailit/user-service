package com.mytestorg.user.application.port.input.user;

import com.mytestorg.user.domain.model.UserModel;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

public interface CreateUserServicePort {
    /**
     * Creates a new user in the system.
     *
     * @param createUserQuery the command object containing user creation details
     * @return a {@link Mono} emitting the created {@link UserModel}
     */
    Mono<UserModel> createUser(final @Valid CreateUserQuery createUserQuery);
}
