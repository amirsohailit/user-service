package com.mytestorg.user.application.port.ouput;

import com.mytestorg.user.application.port.input.user.UpdateUserQuery;
import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.exception.UserNotFoundException;
import reactor.core.publisher.Mono;

public interface UpdateUserPort {
    /**
     * Updates the details of an existing user.
     * <p>
     * Retrieves the user by their unique identifier, applies the updates from the provided {@link UpdateUserQuery}, and saves the changes.
     * If a new password is specified, it is securely encoded; otherwise, the user's existing password is retained.
     * If the user does not exist, emits a {@link UserNotFoundException}.
     * </p>
     *
     * @param userID the unique identifier of the user to update
     * @param req    the {@link UpdateUserQuery} containing the updated user details
     * @return a {@link Mono} emitting the updated {@link UserModel} if successful, or an error if the user does not exist
     */
    Mono<UserModel> updateUser(String userID, UpdateUserQuery req);
}
