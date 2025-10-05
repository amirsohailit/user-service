package com.mytestorg.user.infrastructure.adapter.input.rest.mapper;

import com.mytestorg.user.domain.model.PaginatedUserModel;
import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.adapter.input.rest.dto.response.PaginatedUserResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

public class PaginatedUserResponseMapperTest {

    private PaginatedUserMapper paginatedUserMapper;
    private PaginatedUserResponseMapper paginatedUserResponseMapper;

    private static @NotNull PaginatedUserModel getPaginatedUserModel() {
        UserModel user1 = new UserModel(
                "c2d2273f-4318-40cf-9379-be7d17e29f39",
                "Alice",
                "Smith",
                "Ally",
                "alice@example.com",
                "USA",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        UserModel user2 = new UserModel(
                "c2d2273f-4318-40cf-9379-be7d17e29f32",
                "Bob",
                "Brown",
                "Bobby",
                "bob@example.com",
                "UK",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        List<UserModel> users = List.of(user1, user2);

        return new PaginatedUserModel(
                users,
                2L,
                1,
                2
        );
    }

    @BeforeEach
    void setUp() {
        paginatedUserMapper = mock(PaginatedUserMapper.class);
        paginatedUserResponseMapper = new PaginatedUserResponseMapper(paginatedUserMapper);
    }

    @Test
    void testModelToResponse_withValidModel() {
        PaginatedUserModel paginatedUserModel = getPaginatedUserModel();
        PaginatedUserResponse response = new PaginatedUserResponse(
                paginatedUserModel.users(),
                2L,
                1,
                2
        );
        when(paginatedUserMapper.userModelToUserResponse(paginatedUserModel)).thenReturn(response);

        Mono<PaginatedUserModel> inputMono = Mono.just(paginatedUserModel);

        Mono<PaginatedUserResponse> resultMono = paginatedUserResponseMapper.modelToResponse(inputMono);

        StepVerifier.create(resultMono)
                .expectNext(response)
                .verifyComplete();

        verify(paginatedUserMapper).userModelToUserResponse(paginatedUserModel);
    }

    @Test
    void testModelToResponse_withEmptyMono() {
        Mono<PaginatedUserModel> inputMono = Mono.empty();

        Mono<PaginatedUserResponse> resultMono = paginatedUserResponseMapper.modelToResponse(inputMono);

        StepVerifier.create(resultMono)
                .verifyComplete();

        verifyNoInteractions(paginatedUserMapper);
    }

    @Test
    void testModelToResponse_whenMapperThrowsException() {
        PaginatedUserModel paginatedUserModel = getPaginatedUserModel();
        RuntimeException ex = new RuntimeException("Mapping failed!");

        when(paginatedUserMapper.userModelToUserResponse(paginatedUserModel)).thenThrow(ex);

        Mono<PaginatedUserModel> inputMono = Mono.just(paginatedUserModel);

        Mono<PaginatedUserResponse> resultMono = paginatedUserResponseMapper.modelToResponse(inputMono);

        StepVerifier.create(resultMono)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Mapping failed!"))
                .verify();

        verify(paginatedUserMapper).userModelToUserResponse(paginatedUserModel);
    }
}
