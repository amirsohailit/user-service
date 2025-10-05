package com.mytestorg.user.integrationtest;

import com.mytestorg.user.infrastructure.adapter.output.persistance.entity.UserDocument;
import com.mytestorg.user.infrastructure.adapter.output.persistance.repository.UserReactiveMongoRepository;
import com.mytestorg.user.integrationtest.config.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;

public class ITUserIntegration extends AbstractIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserReactiveMongoRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll().block();
        userRepository.save(UserDocument.builder()
                .id("5baa3598-48d0-4a35-99d7-5ea359cb57d0")
                .firstName("Alice")
                .lastName("Smith")
                .nickname("alices")
                .email("alice@example.com")
                .country("US")
                .password(passwordEncoder.encode("password"))
                .createdAt(Instant.now())
                .build()).block();
    }

    @Test
    void getUser_shouldReturnUserResponse() {
        webTestClient.get()
                .uri("/users/5baa3598-48d0-4a35-99d7-5ea359cb57d0")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo("Alice")
                .jsonPath("$.email").isEqualTo("alice@example.com");
    }
}
