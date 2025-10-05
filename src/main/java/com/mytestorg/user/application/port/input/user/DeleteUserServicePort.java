package com.mytestorg.user.application.port.input.user;

import reactor.core.publisher.Mono;

public interface DeleteUserServicePort {
    /**
     * Deletes a user by their unique identifier.
     *
     * @param id the unique identifier of the user to delete
     * @return a {@link Mono} that completes when the user has been deleted
     */
    Mono<Void> deleteUser(final String id);
}
