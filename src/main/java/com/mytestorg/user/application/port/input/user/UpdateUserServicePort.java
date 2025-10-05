package com.mytestorg.user.application.port.input.user;

import com.mytestorg.user.domain.model.UserModel;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

public interface UpdateUserServicePort {
    /**
     * Get assignments for the given crew member with the given role for the given period
     */
    Mono<UserModel> updateUser(final String id, final @Valid UpdateUserQuery updateUserQuery);
}
