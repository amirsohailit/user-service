package com.mytestorg.user.infrastructure.adapter.input.rest.controller;

import com.mytestorg.user.application.port.input.user.*;
import com.mytestorg.user.application.port.ouput.MessageBrokerServicePort;
import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.adapter.input.rest.config.TestSecurityConfig;
import com.mytestorg.user.infrastructure.adapter.input.rest.dto.request.CreateUserRequest;
import com.mytestorg.user.infrastructure.adapter.input.rest.dto.request.UpdateUserRequest;
import com.mytestorg.user.infrastructure.adapter.input.rest.dto.response.UserResponse;
import com.mytestorg.user.infrastructure.adapter.input.rest.mapper.PaginatedUserResponseMapper;
import com.mytestorg.user.infrastructure.adapter.input.rest.mapper.UserResponseMapper;
import com.mytestorg.user.infrastructure.adapter.output.persistance.entity.UserDocument;
import com.mytestorg.user.infrastructure.adapter.output.persistance.repository.UserNotificationEventReactiveMongoRepository;
import com.mytestorg.user.infrastructure.adapter.output.persistance.repository.UserReactiveMongoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest(UserController.class)
@Import(TestSecurityConfig.class)
public class UserControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private CreateUserServicePort creationServicePort;

    @MockitoBean
    private UserResponseMapper userResponseMapper;

    @MockitoBean
    private UpdateUserServicePort updateUserServicePort;

    @MockitoBean
    private GetUserServicePort getUserServicePort;

    @MockitoBean
    private DeleteUserServicePort deleteUserServicePort;

    @MockitoBean
    private GetAllUserServicePort getAllUserServicePort;

    @MockitoBean
    private PaginatedUserResponseMapper paginatedUserResponseMapper;
    @MockitoBean
    private UserReactiveMongoRepository reactiveMongoRepository;
    @MockitoBean
    private UserNotificationEventReactiveMongoRepository userNotificationEventReactiveMongoRepository;
    @MockitoBean
    private PasswordEncoder passwordEncoder;
    @MockitoBean
    private MessageBrokerServicePort messageBrokerServicePort;

    @Test
    void createUser_ReturnsCreated() {
        CreateUserRequest request = new CreateUserRequest(
                "Alice",
                "Smith",
                "alices",
                "S3cur3P@ssw0rd!",
                "alice.smith@example.com",
                "US"
        );
        String id = UUID.randomUUID().toString();
        CreateUserQuery mockQuery = request.toCreateUserQuery();
        UserModel mockUserModel = new UserModel(
                id,
                "Alice",
                "Smith",
                "alices",
                "alice.smith@example.com",
                "US",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        UserResponse mockResponse = new UserResponse(
                id,
                "Alice",
                "Smith",
                "alices",
                "alice.smith@example.com",
                "US",
                ZonedDateTime.now().toOffsetDateTime(),
                ZonedDateTime.now().toOffsetDateTime()
        );
        UserDocument userDocument = UserDocument.builder()
                .id("1cfd2a2b-0a57-4c1f-8e6c-57e4f7c8e555")
                .firstName("Alice")
                .lastName("Smith")
                .nickname("alices")
                .password("$2a$10$abcdefghijklmnopqrstuv")
                .email("alice.smith@example.com")
                .country("US")
                .createdAt(Instant.parse("2024-06-09T13:21:00Z"))
                .updatedAt(Instant.parse("2024-06-09T13:35:00Z"))
                .build();
        // Set up the mocks
        when(creationServicePort.createUser(mockQuery)).thenReturn(Mono.just(mockUserModel));
        when(reactiveMongoRepository.findByEmail(mockQuery.email())).thenReturn(Mono.empty());
        when(passwordEncoder.encode(mockQuery.email())).thenReturn("HashedPassword");
        when(reactiveMongoRepository.save(any(UserDocument.class))).thenReturn(Mono.just(userDocument));
        when(messageBrokerServicePort.publish(anyString())).thenReturn(Mono.empty());
        when(creationServicePort.createUser(any()))
                .thenReturn(Mono.just(mockUserModel));
        when(userResponseMapper.modelToResponse(any(Mono.class)))
                .thenReturn(Mono.just(mockResponse));

        webTestClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id)
                .jsonPath("$.firstName").isEqualTo("Alice")
                .jsonPath("$.lastName").isEqualTo("Smith")
                .jsonPath("$.nickname").isEqualTo("alices")
                .jsonPath("$.email").isEqualTo("alice.smith@example.com")
                .jsonPath("$.country").isEqualTo("US");
    }

    @Test
    void updateUser_ReturnsOk() {
        String fixedId = "c95d6c96-bb3a-43ed-b9de-519671a68a3c";
        UUID userId = UUID.fromString(fixedId);

        UpdateUserRequest updateRequest = new UpdateUserRequest(
                "Alice",
                "Smith",
                "alicesu",
                "SecurePs@11",
                "alice.smith@example.com",
                "NL"
        );

        UserResponse updatedResponse = new UserResponse(
                fixedId,
                "AliceUpdated",
                "SmithUpdated",
                "alicesu",
                "alice.smith.updated@example.com",
                "US",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        when(updateUserServicePort.updateUser(eq(fixedId), any()))
                .thenReturn(Mono.just(new UserModel(
                        fixedId,
                        "AliceUpdated",
                        "SmithUpdated",
                        "alicesu",
                        "alice.smith.updated@example.com",
                        "US",
                        OffsetDateTime.now(),
                        OffsetDateTime.now()
                )));

        when(userResponseMapper.modelToResponse(any(Mono.class)))
                .thenReturn(Mono.just(updatedResponse));

        webTestClient.put()
                .uri("/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(fixedId)
                .jsonPath("$.firstName").isEqualTo("AliceUpdated")
                .jsonPath("$.lastName").isEqualTo("SmithUpdated")
                .jsonPath("$.nickname").isEqualTo("alicesu")
                .jsonPath("$.email").isEqualTo("alice.smith.updated@example.com")
                .jsonPath("$.country").isEqualTo("US");
    }

    @Test
    void deleteUser_ReturnsNoContent() {
        String fixedId = "c95d6c96-bb3a-43ed-b9de-519671a68a3c";
        UUID userId = UUID.fromString(fixedId);

        when(deleteUserServicePort.deleteUser(fixedId)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/users/{id}", userId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    void getUserById_ReturnsOk() {
        String fixedId = "c95d6c96-bb3a-43ed-b9de-519671a68a3c";
        UUID userId = UUID.fromString(fixedId);

        UserResponse userResponse = new UserResponse(
                fixedId,
                "Alice",
                "Smith",
                "alices",
                "alice.smith@example.com",
                "US",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        when(getUserServicePort.getUser(fixedId))
                .thenReturn(Mono.just(new UserModel(
                        fixedId,
                        "Alice",
                        "Smith",
                        "alices",
                        "alice.smith@example.com",
                        "US",
                        OffsetDateTime.now(),
                        OffsetDateTime.now()
                )));

        when(userResponseMapper.modelToResponse(any(Mono.class)))
                .thenReturn(Mono.just(userResponse));

        webTestClient.get()
                .uri("/users/{id}", userId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(fixedId)
                .jsonPath("$.firstName").isEqualTo("Alice")
                .jsonPath("$.lastName").isEqualTo("Smith")
                .jsonPath("$.nickname").isEqualTo("alices")
                .jsonPath("$.email").isEqualTo("alice.smith@example.com")
                .jsonPath("$.country").isEqualTo("US");
    }
}
