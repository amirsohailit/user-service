package com.mytestorg.user.infrastructure.adapter.input.rest.controller;

import com.mytestorg.user.application.port.input.user.*;
import com.mytestorg.user.infrastructure.adapter.input.rest.EndpointCollection;
import com.mytestorg.user.infrastructure.adapter.input.rest.dto.request.UpdateUserRequest;
import com.mytestorg.user.infrastructure.adapter.input.rest.dto.response.PaginatedUserResponse;
import com.mytestorg.user.infrastructure.adapter.input.rest.dto.request.CreateUserRequest;
import com.mytestorg.user.infrastructure.adapter.input.rest.dto.response.UserResponse;
import com.mytestorg.user.infrastructure.adapter.input.rest.mapper.UserResponseMapper;
import com.mytestorg.user.infrastructure.adapter.input.rest.mapper.PaginatedUserResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(EndpointCollection.USERS)
public class UserController {

    private final CreateUserServicePort creationServicePort;
    private final UserResponseMapper userResponseMapper;
    private final UpdateUserServicePort updateUserServicePort;
    private final GetUserServicePort getUserServicePort;
    private final DeleteUserServicePort deleteUserServicePort;
    private final GetAllUserServicePort getAllUserServicePort;
    private final PaginatedUserResponseMapper paginatedUserResponseMapper;

    /**
     * Creates a new user.
     *
     * @param createUserRequest the request payload containing user details
     * @return a {@link Mono} emitting a {@link ResponseEntity} containing the created user's details
     */

    @PostMapping
    @ResponseBody
    public Mono<ResponseEntity<UserResponse>>  createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
        log.info("Received request to create user with email: {}", createUserRequest.email());
        return userResponseMapper.modelToResponse(creationServicePort.createUser(createUserRequest.toCreateUserQuery()))
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }
    /**
     * Updates an existing user by ID.
     *
     * @param id the UUID of the user to update
     * @param updateUserRequest the request payload containing updated user details
     * @return a {@link Mono} emitting a {@link ResponseEntity} with the updated user's details
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> update(@PathVariable UUID id, @RequestBody @Valid UpdateUserRequest updateUserRequest) {
        log.info("Received request to update user with ID: {}", id);
        return userResponseMapper.modelToResponse(updateUserServicePort.updateUser(id.toString(),updateUserRequest.toUpdateUserQuery()))
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
    /**
     * Deletes a user by ID.
     *
     * @param id the UUID of the user to delete
     * @return a {@link Mono} emitting a {@link ResponseEntity} indicating no content on success
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable UUID id) {
        log.info("Received request to delete user with ID: {}", id);
        return deleteUserServicePort.deleteUser(id.toString())
                .thenReturn(ResponseEntity.noContent().build());
    }
    /**
     * Retrieves a paginated list of users, optionally filtered by country.
     *
     * @param country optional country filter
     * @param page the page number to retrieve (default is 0)
     * @param size the number of users per page (default is 20)
     * @return a {@link Mono} emitting a {@link ResponseEntity} containing the paginated user response
     */
    @GetMapping
    public Mono<ResponseEntity<PaginatedUserResponse>> getUsers(
            @RequestParam(required = false) String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Received request to fetch users with filters - country: '{}', page: {}, size: {}", country, page, size);
        return paginatedUserResponseMapper.modelToResponse(getAllUserServicePort.getUsers(country, page, size))
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
    /**
     * Retrieves a specific user by ID.
     *
     * @param id the UUID of the user to retrieve
     * @return a {@link Mono} emitting a {@link ResponseEntity} containing the user's details
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> getUser(@PathVariable UUID id) {
        log.info("Received request to retrieve user with ID: {}", id);
        return userResponseMapper.modelToResponse(getUserServicePort.getUser(id.toString()))
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}
