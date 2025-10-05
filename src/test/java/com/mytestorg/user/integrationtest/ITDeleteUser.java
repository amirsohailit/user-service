package com.mytestorg.user.integrationtest;

import com.mytestorg.user.infrastructure.adapter.output.persistance.entity.UserDocument;
import com.mytestorg.user.infrastructure.adapter.output.persistance.repository.UserReactiveMongoRepository;
import com.mytestorg.user.integrationtest.config.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ITDeleteUser extends AbstractIntegrationTest {
    private final UUID userId = UUID.randomUUID();
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserReactiveMongoRepository userRepository;

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
                .password("hashedPassword")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        userRepository.save(user).block();
    }

    @Test
    void deleteUser_ShouldReturnNoContent_AndRemoveUser() {
        webTestClient.delete()
                .uri("/users/" + userId)
                .exchange()
                .expectStatus().isNoContent();

        UserDocument deletedUser = userRepository.findById(userId.toString()).block();
        assertThat(deletedUser).isNull();
    }

    @Test
    void deleteUser_NonExistentId_ShouldReturnNotFound() {
        UUID fakeId = UUID.randomUUID();

        webTestClient.delete()
                .uri("/users/" + fakeId)
                .exchange()
                .expectStatus().isNotFound();
    }
}
