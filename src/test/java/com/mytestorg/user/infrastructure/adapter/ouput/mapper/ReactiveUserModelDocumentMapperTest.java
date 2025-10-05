package com.mytestorg.user.infrastructure.adapter.ouput.mapper;

import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.adapter.output.persistance.entity.UserDocument;
import com.mytestorg.user.infrastructure.adapter.output.persistance.mapper.ReactiveUserModelDocumentMapper;
import com.mytestorg.user.infrastructure.adapter.output.persistance.mapper.UserModelDocumentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.OffsetDateTime;

import static org.mockito.Mockito.*;

public class ReactiveUserModelDocumentMapperTest {

    @Mock
    private UserModelDocumentMapper userMapper;

    @InjectMocks
    private ReactiveUserModelDocumentMapper reactiveMapper;

    private UserDocument buildUserDocument() {
        return UserDocument.builder()
                .id("1")
                .firstName("Alice")
                .lastName("Smith")
                .nickname("alices")
                .email("alice@example.com")
                .country("US")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private UserModel buildUserModel() {
        return new UserModel(
                "1", "Alice", "Smith", "alices",
                "alice@example.com", "US",
                OffsetDateTime.now(), OffsetDateTime.now()
        );
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reactiveMapper = new ReactiveUserModelDocumentMapper(userMapper);
    }

    @Test
    void toModel_shouldMapMonoUserDocumentToMonoUserModel() {
        UserDocument doc = buildUserDocument();
        UserModel model = buildUserModel();
        when(userMapper.userDocumentToUserModel(doc)).thenReturn(model);

        Mono<UserModel> result = reactiveMapper.toModel(Mono.just(doc));

        StepVerifier.create(result)
                .expectNext(model)
                .verifyComplete();

        verify(userMapper).userDocumentToUserModel(doc);
    }

    @Test
    void toDocument_shouldMapMonoUserModelToMonoUserDocument() {
        UserModel model = buildUserModel();
        UserDocument doc = buildUserDocument();
        when(userMapper.userModelToUserDocument(model)).thenReturn(doc);

        Mono<UserDocument> result = reactiveMapper.toDocument(Mono.just(model));

        StepVerifier.create(result)
                .expectNext(doc)
                .verifyComplete();

        verify(userMapper).userModelToUserDocument(model);
    }

    @Test
    void toModel_shouldPropagateEmptyMono() {
        StepVerifier.create(reactiveMapper.toModel(Mono.empty()))
                .verifyComplete();
        verifyNoInteractions(userMapper);
    }

    @Test
    void toDocument_shouldPropagateEmptyMono() {
        StepVerifier.create(reactiveMapper.toDocument(Mono.empty()))
                .verifyComplete();
        verifyNoInteractions(userMapper);
    }
}
