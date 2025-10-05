package com.mytestorg.user.application.port.ouput;

import com.mytestorg.user.domain.model.PaginatedUserModel;
import reactor.core.publisher.Mono;

public interface GetAllUserPort {
    /**
     * Retrieves a paginated list of users, optionally filtered by country.
     * <p>
     * If a country is specified, only users from that country are returned; otherwise, all users are retrieved.
     * The result includes the list of users for the requested page and the total user count for pagination purposes.
     * </p>
     *
     * @param country the country to filter users by (may be {@code null} or blank to retrieve users from all countries)
     * @param page    the zero-based page number to retrieve
     * @param size    the number of users per page
     * @return a {@link Mono} emitting a {@link PaginatedUserModel} containing the paginated list of users and total count
     */
    Mono<PaginatedUserModel> getUsers(String country, int page, int size);
}
