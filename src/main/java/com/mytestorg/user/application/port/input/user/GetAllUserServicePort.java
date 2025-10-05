package com.mytestorg.user.application.port.input.user;

import com.mytestorg.user.domain.model.PaginatedUserModel;
import reactor.core.publisher.Mono;

public interface GetAllUserServicePort {
    /**
     * Retrieves a paginated list of users, optionally filtered by country.
     *
     * @param country the country to filter users by (may be {@code null} to retrieve users from all countries)
     * @param page    the page number to retrieve (zero-based)
     * @param size    the number of users per page
     * @return a {@link Mono} emitting a {@link PaginatedUserModel} containing the users and pagination details
     */
    Mono<PaginatedUserModel> getUsers(String country, int page, int size);
}
