package com.mytestorg.user.infrastructure.adapter.ouput.persistance.repository;

import com.mytestorg.user.infrastructure.adapter.output.persistance.entity.UserDocument;
import com.mytestorg.user.infrastructure.adapter.output.persistance.repository.UserReactiveMongoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class UserReactiveRepositoryTest {
    private final String userId = "abc123";
    private final String email = "john@example.com";
    private final String country = "US";
    private final UserDocument sampleUser = UserDocument.builder()
            .id(userId)
            .firstName("John")
            .lastName("Doe")
            .nickname("johnd")
            .email(email)
            .password("hashedPass")
            .country(country)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    @Mock
    private UserReactiveMongoRepository userRepository;

    @Test
    void findByEmail_ShouldReturnUser() {
        when(userRepository.findByEmail(email)).thenReturn(Mono.just(sampleUser));

        StepVerifier.create(userRepository.findByEmail(email))
                .expectNext(sampleUser)
                .verifyComplete();
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenUserNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Mono.empty());

        StepVerifier.create(userRepository.findByEmail("notfound@example.com"))
                .verifyComplete();
    }

    @Test
    void findAllByCountry_ShouldReturnUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findAllByCountry(country, pageable)).thenReturn(Flux.just(sampleUser));

        StepVerifier.create(userRepository.findAllByCountry(country, pageable))
                .expectNext(sampleUser)
                .verifyComplete();
    }

    @Test
    void findAllByCountry_ShouldReturnEmpty_WhenNoUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findAllByCountry("CA", pageable)).thenReturn(Flux.empty());

        StepVerifier.create(userRepository.findAllByCountry("CA", pageable))
                .verifyComplete();
    }

    @Test
    void countByCountry_ShouldReturnCount() {
        when(userRepository.countByCountry(country)).thenReturn(Mono.just(1L));

        StepVerifier.create(userRepository.countByCountry(country))
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void countByCountry_ShouldReturnZero_WhenNoUsers() {
        when(userRepository.countByCountry("CA")).thenReturn(Mono.just(0L));

        StepVerifier.create(userRepository.countByCountry("CA"))
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void findAllByPageable_ShouldReturnAllUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findAllBy(pageable)).thenReturn(Flux.just(sampleUser));

        StepVerifier.create(userRepository.findAllBy(pageable))
                .expectNext(sampleUser)
                .verifyComplete();
    }

    @Test
    void findById_ShouldReturnUser() {
        when(userRepository.findById(userId)).thenReturn(Mono.just(sampleUser));

        StepVerifier.create(userRepository.findById(userId))
                .expectNext(sampleUser)
                .verifyComplete();
    }

    @Test
    void deleteById_ShouldComplete() {
        when(userRepository.deleteById(userId)).thenReturn(Mono.empty());

        StepVerifier.create(userRepository.deleteById(userId))
                .verifyComplete();
    }
}
