package com.mytestorg.user.infrastructure.adapter.ouput.persistance;

import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.adapter.output.persistance.DeleteUserReactivePersistenceAdapter;
import com.mytestorg.user.infrastructure.adapter.output.persistance.entity.UserDocument;
import com.mytestorg.user.infrastructure.adapter.output.persistance.mapper.UserModelDocumentMapper;
import com.mytestorg.user.infrastructure.adapter.output.persistance.repository.UserReactiveMongoRepository;
import com.mytestorg.user.infrastructure.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DeleteUserReactivePersistenceAdapterTest {

    private final String USER_ID = "test-user-id";
    @Mock
    private UserReactiveMongoRepository userReactiveMongoRepository;
    @Mock
    private UserModelDocumentMapper userModelDocumentMapper;
    @InjectMocks
    private DeleteUserReactivePersistenceAdapter adapter;

    private UserDocument sampleUser() {
        return UserDocument.builder()
                .id(USER_ID)
                .firstName("Alice")
                .lastName("Smith")
                .nickname("Alice")
                .email("alice@example.com")
                .country("NL")
                .createdAt(null)
                .build();
    }

    @Test
    void deleteUser_whenUserExists_shouldDeleteAndPublishEvent() {
        UserDocument userDoc = sampleUser();
        UserModel userModel = mapUserModel(userDoc);
        when(userReactiveMongoRepository.findById(USER_ID))
                .thenReturn(Mono.just(userDoc));
        when(userReactiveMongoRepository.deleteById(USER_ID))
                .thenReturn(Mono.empty());
        when(userModelDocumentMapper.userDocumentToUserModel(userDoc))
                .thenReturn(userModel);
        StepVerifier.create(adapter.deleteUser("test-user-id"))
                .expectNextMatches(user ->
                        user.id().equals("test-user-id") &&
                                user.firstName().equals("Alice"))
                .expectComplete()
                .verify();

        verify(userReactiveMongoRepository).findById(USER_ID);
        verify(userReactiveMongoRepository).deleteById(USER_ID);
    }

    private UserModel mapUserModel(UserDocument userDoc) {
        return new UserModel(userDoc.getId(), userDoc.getFirstName(), userDoc.getLastName(), userDoc.getNickname(), userDoc.getEmail(), userDoc.getCountry(), null, null);
    }

    @Test
    void deleteUser_whenUserDoesNotExist_shouldError() {
        when(userReactiveMongoRepository.findById(USER_ID))
                .thenReturn(Mono.empty());
        when(userModelDocumentMapper.userDocumentToUserModel(any()))
                .thenReturn(any(UserModel.class));
        StepVerifier.create(adapter.deleteUser(USER_ID))
                .expectErrorSatisfies(error -> {
                    assert error instanceof UserNotFoundException;
                    assert error.getMessage().contains("User can't be deleted");
                })
                .verify();

        verify(userReactiveMongoRepository).findById(USER_ID);
        verify(userReactiveMongoRepository, never()).deleteById(anyString());
    }
}