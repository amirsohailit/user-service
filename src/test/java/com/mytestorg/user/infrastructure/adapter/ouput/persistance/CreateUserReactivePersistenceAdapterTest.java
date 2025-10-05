package com.mytestorg.user.infrastructure.adapter.ouput.persistance;

import com.mytestorg.user.application.port.input.user.CreateUserQuery;
import com.mytestorg.user.application.port.ouput.MessageBrokerServicePort;
import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.adapter.output.messaging.kafka.KafkaEventMessage;
import com.mytestorg.user.infrastructure.adapter.output.persistance.CreateUserReactivePersistenceAdapter;
import com.mytestorg.user.infrastructure.adapter.output.persistance.entity.UserDocument;
import com.mytestorg.user.infrastructure.adapter.output.persistance.mapper.ReactiveUserModelDocumentMapper;
import com.mytestorg.user.infrastructure.adapter.output.persistance.repository.UserNotificationEventReactiveMongoRepository;
import com.mytestorg.user.infrastructure.adapter.output.persistance.repository.UserReactiveMongoRepository;
import com.mytestorg.user.infrastructure.exception.UserAlreadyExistsException;
import com.mytestorg.user.infrastructure.util.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CreateUserReactivePersistenceAdapterTest {

    @Mock
    private UserReactiveMongoRepository userReactiveMongoRepository;
    @Mock
    private UserNotificationEventReactiveMongoRepository userNotificationEventReactiveMongoRepository;
    @Mock
    private ReactiveUserModelDocumentMapper userModelDocumentMapper;
    @Mock
    private MessageBrokerServicePort messageBrokerServicePort;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JsonUtil jsonUtil;
    @InjectMocks
    private CreateUserReactivePersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new CreateUserReactivePersistenceAdapter(
                userReactiveMongoRepository,
                userModelDocumentMapper,
                passwordEncoder
        );
    }

    private CreateUserQuery buildCreateUserQuery() {
        return new CreateUserQuery(
                UUID.randomUUID().toString(),
                "Alice",
                "Smith",
                "alices",
                "S3cur3P@ssw0rd!",
                "alice.smith@example.com",
                "US",
                OffsetDateTime.now(),
                null
        );
    }

    private UserDocument buildUserDocument(CreateUserQuery query, String encodedPassword) {
        return UserDocument.builder()
                .id(query.id())
                .firstName(query.firstName())
                .lastName(query.lastName())
                .nickname(query.nickname())
                .email(query.email())
                .country(query.country())
                .password(encodedPassword)
                .createdAt(query.createdAt().toInstant())
                .updatedAt(null)
                .build();
    }

    private UserModel buildUserModel(CreateUserQuery query) {
        return new UserModel(
                query.id(),
                query.firstName(),
                query.lastName(),
                query.nickname(),
                query.email(),
                query.country(),
                query.createdAt(),
                null
        );
    }

    @Test
    void createUser_UserAlreadyExists_ShouldThrowException() {
        CreateUserQuery query = buildCreateUserQuery();
        UserDocument existingUser = buildUserDocument(query, "hashedPassword");
        Mockito.when(userReactiveMongoRepository.findByEmail(any()))
                .thenReturn(Mono.just(existingUser));
        Mockito.when(userReactiveMongoRepository.save(any()))
                .thenReturn(Mono.just(existingUser));
        Mockito.when(userModelDocumentMapper.toModel(any()))
                .thenReturn(Mono.empty());
        StepVerifier.create(adapter.createUser(query))
                .expectErrorSatisfies(ex -> {
                    assert ex instanceof UserAlreadyExistsException;
                    assert ex.getMessage().contains("User email address already exists");
                })
                .verify();

        verify(userReactiveMongoRepository).findByEmail(query.email());
        verifyNoInteractions(messageBrokerServicePort, jsonUtil);
    }

    @Test
    void createUser_UserDoesNotExist_ShouldCreateUserAndPublishEvent() {
        CreateUserQuery query = buildCreateUserQuery();
        String encodedPassword = "hashedPassword";
        UserDocument savedDoc = buildUserDocument(query, encodedPassword);
        UserModel userModel = buildUserModel(query);

        Mockito.when(userReactiveMongoRepository.findByEmail(query.email()))
                .thenReturn(Mono.empty());
        Mockito.when(passwordEncoder.encode(query.password()))
                .thenReturn(encodedPassword);
        Mockito.when(userReactiveMongoRepository.save(any(UserDocument.class)))
                .thenReturn(Mono.just(savedDoc));
        Mockito.when(jsonUtil.toJson(any()))
                .thenReturn("{\"event\":\"user_created\"}");
        Mockito.when(messageBrokerServicePort.publish(anyString()))
                .thenReturn(Mono.empty());
        Mockito.when(userModelDocumentMapper.toModel(Mono.just(savedDoc)))
                .thenAnswer(invocation -> {
                    Mono<UserDocument> docMono = invocation.getArgument(0);
                    return docMono.map(doc -> userModel);
                });
        Mockito.when(userModelDocumentMapper.toModel(any())).thenReturn(Mono.just(userModel));
        StepVerifier.create(adapter.createUser(query))
                .expectNext(userModel)
                .verifyComplete();

        verify(userReactiveMongoRepository).findByEmail(query.email());
        verify(passwordEncoder).encode(query.password());
        verify(userReactiveMongoRepository).save(any(UserDocument.class));
        verify(jsonUtil, times(0)).toJson(any(KafkaEventMessage.class));
        verify(messageBrokerServicePort, times(0)).publish(anyString());
        verify(userModelDocumentMapper).toModel(any(Mono.class));
    }
}
