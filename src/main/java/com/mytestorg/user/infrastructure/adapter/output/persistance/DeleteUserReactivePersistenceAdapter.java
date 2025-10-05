package com.mytestorg.user.infrastructure.adapter.output.persistance;

import com.mytestorg.user.application.port.ouput.DeleteUserPort;
import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.adapter.output.persistance.mapper.UserModelDocumentMapper;
import com.mytestorg.user.infrastructure.adapter.output.persistance.repository.UserReactiveMongoRepository;
import com.mytestorg.user.infrastructure.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class DeleteUserReactivePersistenceAdapter implements DeleteUserPort {
    private final UserReactiveMongoRepository userReactiveMongoRepository;
    private final UserModelDocumentMapper userModelDocumentMapper;

    public Mono<UserModel> deleteUser(String userID) {
        return userReactiveMongoRepository.findById(userID)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User can't be deleted as it doesn't exist")))
                .flatMap(userEntityToBeDeleted ->
                        userReactiveMongoRepository.deleteById(userID)
                                .thenReturn(userEntityToBeDeleted))
                .map(userModelDocumentMapper::userDocumentToUserModel);
    }
}
