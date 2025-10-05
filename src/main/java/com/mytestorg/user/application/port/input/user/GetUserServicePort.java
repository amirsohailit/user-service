package com.mytestorg.user.application.port.input.user;

import com.mytestorg.user.domain.model.UserModel;
import reactor.core.publisher.Mono;

public interface GetUserServicePort {
    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user to retrieve
     * @return a {@link Mono} emitting the {@link UserModel} if found, or empty if not found
     */
    Mono<UserModel> getUser(final String id);
}
