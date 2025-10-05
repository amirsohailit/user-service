package com.mytestorg.user.infrastructure.adapter.ouput.persistance;
import com.mytestorg.user.infrastructure.adapter.output.persistance.entity.UserDocument;
import com.mytestorg.user.infrastructure.adapter.output.persistance.mapper.ReactiveUserModelDocumentMapper;
import com.mytestorg.user.infrastructure.adapter.output.persistance.repository.UserReactiveMongoRepository;
import com.mytestorg.user.infrastructure.adapter.output.persistance.GetUserReactivePersistenceAdapter;
import com.mytestorg.user.infrastructure.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.UUID;
import com.mytestorg.user.domain.model.UserModel;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class GetUserReactivePersistenceAdapterTest {

    @Mock
    private UserReactiveMongoRepository userReactiveMongoRepository;
    @Mock
    private ReactiveUserModelDocumentMapper userModelDocumentMapper;

    @InjectMocks
    private GetUserReactivePersistenceAdapter adapter;

    private final String USER_ID = UUID.randomUUID().toString();

    private UserDocument buildUserDocument() {
        return UserDocument.builder()
                .id(USER_ID)
                .firstName("Alice")
                .lastName("Smith")
                .nickname("alices")
                .email("alice@example.com")
                .country("US")
                .createdAt(OffsetDateTime.now().toInstant())
                .build();
    }

    private UserModel buildUserModel() {
        return new UserModel(
                USER_ID, "Alice", "Smith", "alices",
                "alice@example.com", "US",
                OffsetDateTime.now(), null
        );
    }

    @Test
    void getUser_whenUserExists_shouldReturnUserModel() {
        UserDocument userDoc = buildUserDocument();
        UserModel userModel = buildUserModel();

        when(userReactiveMongoRepository.findById(USER_ID))
                .thenReturn(Mono.just(userDoc));
        when(userModelDocumentMapper.toModel(any(Mono.class)))
                .thenAnswer(invocation -> {
                    Mono<UserDocument> docMono = invocation.getArgument(0);
                    return docMono.map(doc -> userModel);
                });

        StepVerifier.create(adapter.getUser(USER_ID))
                .expectNext(userModel)
                .verifyComplete();

        verify(userReactiveMongoRepository).findById(USER_ID);
        verify(userModelDocumentMapper).toModel(any(Mono.class));
    }

    @Test
    void getUser_whenUserDoesNotExist_shouldErrorWithUserNotFoundException() {
        when(userReactiveMongoRepository.findById(USER_ID))
                .thenReturn(Mono.empty());
        when(userModelDocumentMapper.toModel(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        StepVerifier.create(adapter.getUser(USER_ID))
                .expectErrorSatisfies(error -> {
                    assert error instanceof UserNotFoundException;
                    assert error.getMessage().contains("User not found");
                })
                .verify();

        verify(userReactiveMongoRepository).findById(USER_ID);
    }
}