package com.mytestorg.user.infrastructure.adapter.output.persistance;

import com.mytestorg.user.application.port.input.user.CreateUserQuery;
import com.mytestorg.user.application.port.ouput.CreateUserPort;
import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.adapter.output.persistance.entity.UserDocument;
import com.mytestorg.user.infrastructure.adapter.output.persistance.mapper.ReactiveUserModelDocumentMapper;
import com.mytestorg.user.infrastructure.adapter.output.persistance.repository.UserReactiveMongoRepository;
import com.mytestorg.user.infrastructure.exception.UserAlreadyExistsException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class CreateUserReactivePersistenceAdapter implements CreateUserPort {
    private final UserReactiveMongoRepository userReactiveMongoRepository;
    private final ReactiveUserModelDocumentMapper userModelDocumentMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserModel> createUser(CreateUserQuery req) {
        return userReactiveMongoRepository.findByEmail(req.email())
                .flatMap(existing -> Mono.error(new UserAlreadyExistsException("User email address already exists, User can't be registered with the following email address: " + req.email())))
                .switchIfEmpty(
                        userReactiveMongoRepository.save(UserDocument.builder()
                                        .id(req.id())
                                        .firstName(req.firstName())
                                        .lastName(req.lastName())
                                        .email(req.email())
                                        .password(passwordEncoder.encode(req.password()))
                                        .nickname(req.nickname())
                                        .country(req.country())
                                        .createdAt(req.createdAt().toInstant())
                                        .updatedAt(null)
                                        .build()
                                )
                                .transform(userModelDocumentMapper::toModel)
                )
                .cast(UserModel.class);
    }

}
