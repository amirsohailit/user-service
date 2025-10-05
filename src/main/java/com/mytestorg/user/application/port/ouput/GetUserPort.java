package com.mytestorg.user.application.port.ouput;

import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.exception.UserNotFoundException;
import reactor.core.publisher.Mono;

public interface GetUserPort {
    /**
     * Retrieves a user by their unique identifier.
     * <p>
     * If no user is found with the given ID, emits a {@link UserNotFoundException}.
     * </p>
     *
     * @param userID the unique identifier of the user to retrieve
     * @return a {@link Mono} emitting the {@link UserModel} if found, or an error if not found
     */
    Mono<UserModel> getUser(String userID);
}
