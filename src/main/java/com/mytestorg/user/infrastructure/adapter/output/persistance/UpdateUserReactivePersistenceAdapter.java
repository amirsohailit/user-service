package com.mytestorg.user.infrastructure.adapter.output.persistance;

import com.mytestorg.user.application.port.input.user.UpdateUserQuery;
import com.mytestorg.user.application.port.ouput.UpdateUserPort;
import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.adapter.output.persistance.entity.UserDocument;
import com.mytestorg.user.infrastructure.adapter.output.persistance.mapper.ReactiveUserModelDocumentMapper;
import com.mytestorg.user.infrastructure.adapter.output.persistance.repository.UserReactiveMongoRepository;
import com.mytestorg.user.infrastructure.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
@AllArgsConstructor
@Slf4j
public class UpdateUserReactivePersistenceAdapter implements UpdateUserPort {
    private final UserReactiveMongoRepository userReactiveMongoRepository;
    private final ReactiveUserModelDocumentMapper userModelDocumentMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserModel> updateUser(String userID, UpdateUserQuery req) {
        return userReactiveMongoRepository.findById(userID)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found with userId:"+userID)))
                .flatMap(existingUser -> {
                    UserDocument updatedUser = UserDocument.builder()
                            .id(userID)
                            .firstName(req.firstName())
                            .lastName(req.lastName())
                            .email(req.email())
                            .password(req.password() != null && !req.password().isEmpty()
                                    ? passwordEncoder.encode(req.password())
                                    : existingUser.getPassword())
                            .nickname(req.nickname())
                            .country(req.country())
                            .createdAt(existingUser.getCreatedAt())
                            .updatedAt(Instant.now())
                            .build();

                    return userReactiveMongoRepository.save(updatedUser);
                })
                .transform(userModelDocumentMapper::toModel);
    }

}
