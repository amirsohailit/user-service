package com.mytestorg.user.application.port.ouput;

import com.mytestorg.user.application.port.input.user.CreateUserQuery;
import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.exception.UserAlreadyExistsException;
import reactor.core.publisher.Mono;

public interface CreateUserPort {
    /**
     * Creates a new user in the system.
     * <p>
     * If a user with the given email address already exists, emits a {@link UserAlreadyExistsException}.
     * On successful creation, the user's password is securely encoded before storing.
     * </p>
     *
     * @param req the {@link CreateUserQuery} containing the details for the new user
     * @return a {@link Mono} emitting the created {@link UserModel}
     */
    Mono<UserModel> createUser(CreateUserQuery req);
}
