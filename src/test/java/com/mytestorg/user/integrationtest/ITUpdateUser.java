package com.mytestorg.user.integrationtest;

import com.mytestorg.user.infrastructure.adapter.input.rest.dto.request.UpdateUserRequest;
import com.mytestorg.user.infrastructure.adapter.input.rest.dto.response.UserResponse;
import com.mytestorg.user.infrastructure.adapter.output.persistance.entity.UserDocument;
import com.mytestorg.user.infrastructure.adapter.output.persistance.repository.UserReactiveMongoRepository;
import com.mytestorg.user.integrationtest.config.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ITUpdateUser extends AbstractIntegrationTest {
    private final UUID userId = UUID.randomUUID();
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserReactiveMongoRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll().block();

        UserDocument user = UserDocument.builder()
                .id(userId.toString())
                .firstName("Alice")
                .lastName("Smith")
                .nickname("alices")
                .email("alice@example.com")
                .country("US")
                .password(passwordEncoder.encode("hashedPassword"))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        userRepository.save(user).block();
    }


    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        UpdateUserRequest request = new UpdateUserRequest(
                "AliceUpdated",
                "SmithUpdated",
                "alicesUpdated",
                "newPassword@123",
                "alice.updated@example.com",
                "NL"
        );

        webTestClient.put()
                .uri("/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponse.class)
                .value(response -> {
                    assertThat(response.id()).isEqualTo(userId.toString());
                    assertThat(response.firstName()).isEqualTo("AliceUpdated");
                    assertThat(response.lastName()).isEqualTo("SmithUpdated");
                    assertThat(response.nickname()).isEqualTo("alicesUpdated");
                    assertThat(response.email()).isEqualTo("alice.updated@example.com");
                    assertThat(response.country()).isEqualTo("NL");
                });
    }
}
