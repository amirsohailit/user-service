package com.mytestorg.user.infrastructure.adapter.output.persistance;

import com.mytestorg.user.application.port.ouput.GetUserPort;
import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.adapter.output.persistance.mapper.ReactiveUserModelDocumentMapper;
import com.mytestorg.user.infrastructure.adapter.output.persistance.repository.UserReactiveMongoRepository;
import com.mytestorg.user.infrastructure.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class GetUserReactivePersistenceAdapter implements GetUserPort {
    private final UserReactiveMongoRepository userReactiveMongoRepository;
    private final ReactiveUserModelDocumentMapper userModelDocumentMapper;

    @Override
    public Mono<UserModel> getUser(String userID) {
        return userReactiveMongoRepository.findById(userID)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found")))
                .transform(userModelDocumentMapper::toModel);
    }

}
