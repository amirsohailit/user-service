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
import java.util.List;
import java.util.UUID;

public class ITGetUsers extends AbstractIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserReactiveMongoRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll().block();
        List<UserDocument> users = List.of(
                buildUser("John", "Doe", "jdoe", "john@example.com", "US"),
                buildUser("Alice", "Smith", "asmith", "alice@example.com", "US"),
                buildUser("Bob", "Brown", "bbrown", "bob@example.com", "NL")
        );

        userRepository.saveAll(users).collectList().block();
    }

    @Test
    void getUsers_ShouldReturnPaginatedUsers_FilteredByCountry() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users")
                        .queryParam("country", "US")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.users.length()").isEqualTo(2)
                .jsonPath("$.users[0].country").isEqualTo("US")
                .jsonPath("$.users[1].country").isEqualTo("US");
    }

    @Test
    void getUsers_ShouldReturnAll_WhenNoCountryProvided() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.users.length()").isEqualTo(3);
    }

    private UserDocument buildUser(String first, String last, String nick, String email, String country) {
        return UserDocument.builder()
                .id(UUID.randomUUID().toString())
                .firstName(first)
                .lastName(last)
                .nickname(nick)
                .email(email)
                .country(country)
                .password(passwordEncoder.encode("securePassword123@"))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
