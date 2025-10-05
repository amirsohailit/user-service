package com.mytestorg.user.infrastructure.adapter.ouput.persistance;

import com.mytestorg.user.application.port.input.user.UpdateUserQuery;
import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.adapter.output.persistance.UpdateUserReactivePersistenceAdapter;
import com.mytestorg.user.infrastructure.adapter.output.persistance.entity.UserDocument;
import com.mytestorg.user.infrastructure.adapter.output.persistance.mapper.ReactiveUserModelDocumentMapper;
import com.mytestorg.user.infrastructure.adapter.output.persistance.repository.UserReactiveMongoRepository;
import com.mytestorg.user.infrastructure.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

class UpdateUserReactivePersistenceAdapterTest {

    @Mock
    private UserReactiveMongoRepository userReactiveMongoRepository;
    @Mock
    private ReactiveUserModelDocumentMapper userModelDocumentMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UpdateUserReactivePersistenceAdapter adapter;

    private final String USER_ID = "test-user-id";

    private UserDocument existingUser() {
        return UserDocument.builder()
                .id(USER_ID)
                .firstName("Alice")
                .lastName("Smith")
                .nickname("alices")
                .email("alice@example.com")
                .password("oldHash")
                .country("US")
                .createdAt(Instant.now().minusSeconds(3600))
                .updatedAt(null)
                .build();
    }

    private UpdateUserQuery buildUpdateUserQuery(String password) {
        return new UpdateUserQuery(
                UUID.randomUUID(),
                "Smithson",
                "ally",
                password,
                "alicia@example.com",
                "CA",
                "NL",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
    }

    private UserDocument updatedUserDocument(UserDocument oldDoc, UpdateUserQuery req, String encodedPwd) {
        return UserDocument.builder()
                .id(oldDoc.getId())
                .firstName(req.firstName())
                .lastName(req.lastName())
                .nickname(req.nickname())
                .email(req.email())
                .password(encodedPwd)
                .country(req.country())
                .createdAt(oldDoc.getCreatedAt())
                .updatedAt(any(Instant.class)) // just for structure, not for assert
                .build();
    }

    private UserModel buildUserModel(UserDocument updatedDoc) {
        return new UserModel(
                updatedDoc.getId(),
                updatedDoc.getFirstName(),
                updatedDoc.getLastName(),
                updatedDoc.getNickname(),
                updatedDoc.getEmail(),
                updatedDoc.getCountry(),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
    }

    @Test
    void updateUser_whenUserExists_shouldUpdateAndPublishEvent() {
        UserDocument oldDoc = existingUser();
        UpdateUserQuery req = buildUpdateUserQuery("newPassword");
        String encodedPwd = "newEncodedHash";
        UserDocument savedDoc = UserDocument.builder()
                .id(USER_ID)
                .firstName(req.firstName())
                .lastName(req.lastName())
                .nickname(req.nickname())
                .email(req.email())
                .password(encodedPwd)
                .country(req.country())
                .createdAt(oldDoc.getCreatedAt())
                .updatedAt(OffsetDateTime.now().toInstant())
                .build();
        UserModel expectedModel = buildUserModel(savedDoc);

        when(userReactiveMongoRepository.findById(anyString()))
                .thenReturn(Mono.just(oldDoc));
        when(passwordEncoder.encode(anyString()))
                .thenReturn(encodedPwd);
        when(userReactiveMongoRepository.save(any(UserDocument.class)))
                .thenReturn(Mono.just(savedDoc));
        when(userModelDocumentMapper.toModel(any(Mono.class))).thenReturn(Mono.just(expectedModel));
                //.thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(adapter.updateUser(anyString(), req))
                .expectNext(expectedModel)
                .verifyComplete();

        verify(userReactiveMongoRepository).findById(anyString());
        verify(passwordEncoder, times(0)).encode(anyString());
        verify(userModelDocumentMapper).toModel(any(Mono.class));
    }

    @Test
    void updateUser_whenUserDoesNotExist_shouldErrorWithUserNotFoundException() {
        UpdateUserQuery req = buildUpdateUserQuery("anyPassword");
        when(userReactiveMongoRepository.findById(USER_ID)).thenReturn(Mono.empty());
        when(userModelDocumentMapper.toModel(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(adapter.updateUser(USER_ID, req))
                .expectErrorSatisfies(error -> {
                    assert error instanceof UserNotFoundException;
                    assert error.getMessage().contains("User not found");
                })
                .verify();

        verify(userReactiveMongoRepository).findById(USER_ID);
    }

    @Test
    void updateUser_whenPasswordIsNullOrEmpty_shouldKeepOldPassword() {
        UserDocument oldDoc = existingUser();
        UpdateUserQuery req = buildUpdateUserQuery(null);
        UserDocument savedDoc = UserDocument.builder()
                .id(USER_ID)
                .firstName(req.firstName())
                .lastName(req.lastName())
                .nickname(req.nickname())
                .email(req.email())
                .password(oldDoc.getPassword())
                .country(req.country())
                .createdAt(oldDoc.getCreatedAt())
                .updatedAt(OffsetDateTime.now().toInstant())
                .build();
        UserModel expectedModel = buildUserModel(savedDoc);

        when(userReactiveMongoRepository.findById(anyString()))
                .thenReturn(Mono.just(oldDoc));
        when(userReactiveMongoRepository.save(any(UserDocument.class)))
                .thenReturn(Mono.just(savedDoc));
        when(userModelDocumentMapper.toModel(any(Mono.class)))
                .thenReturn(Mono.just(expectedModel));

        StepVerifier.create(adapter.updateUser("Test", req))
                .expectNext(expectedModel)
                .verifyComplete();

        verify(userReactiveMongoRepository).findById(anyString());

        verify(passwordEncoder, never()).encode(anyString());
    }

}