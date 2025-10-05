package com.mytestorg.user.integrationtest;

import com.mytestorg.user.infrastructure.adapter.input.rest.dto.request.CreateUserRequest;
import com.mytestorg.user.infrastructure.adapter.output.persistance.repository.UserReactiveMongoRepository;
import com.mytestorg.user.integrationtest.config.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

public class ITCreateUser extends AbstractIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserReactiveMongoRepository userRepository;

    @BeforeEach
    void setUp() {

        userRepository.deleteAll().block();
    }


    @Test
    void shouldCreateUserSuccessfully() {
        CreateUserRequest request = new CreateUserRequest("Alice", "Smith", "alices", "S3cur3P@ssw0rd!", "alice.smith@example.com", "US");

        webTestClient
                .post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.id")
                .isNotEmpty()
                .jsonPath("$.firstName")
                .isEqualTo("Alice")
                .jsonPath("$.lastName")
                .isEqualTo("Smith")
                .jsonPath("$.nickname")
                .isEqualTo("alices")
                .jsonPath("$.email")
                .isEqualTo("alice.smith@example.com")
                .jsonPath("$.country")
                .isEqualTo("US")
                .jsonPath("$.createdAt").isNotEmpty()
                .jsonPath("$.updatedAt").doesNotExist();
    }

    @Test
    void shouldReturnBadRequest_WhenMissingEmail() {
        CreateUserRequest invalidRequest = new CreateUserRequest("Alice", "Smith", "alices", "S3cur3P@ssw0rd!", "", // Invalid email
                "US");

        webTestClient
                .post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void shouldReturnBadRequest_WhenPasswordCriteriaNotMet() {
        CreateUserRequest invalidRequest = new CreateUserRequest("Alice", "Smith", "alices", "basicpassword", "alice.smith@example.com", // Invalid email
                "US");

        webTestClient
                .post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }
}
