package com.mytestorg.user.application.port.ouput;

import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.exception.UserNotFoundException;
import reactor.core.publisher.Mono;

public interface DeleteUserPort {
    /**
     * Deletes a user by their unique identifier.
     * <p>
     * If no user is found with the given ID, emits a {@link UserNotFoundException}.
     * On successful deletion, returns the deleted user's model.
     * </p>
     *
     * @param userID the unique identifier of the user to delete
     * @return a {@link Mono} emitting the {@link UserModel} of the deleted user,
     * or an error if the user does not exist
     */
    Mono<UserModel> deleteUser(String userID);
}
