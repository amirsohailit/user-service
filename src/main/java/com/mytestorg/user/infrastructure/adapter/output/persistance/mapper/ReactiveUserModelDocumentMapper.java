package com.mytestorg.user.infrastructure.adapter.output.persistance.mapper;

import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.adapter.output.persistance.entity.UserDocument;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class ReactiveUserModelDocumentMapper {
    private final UserModelDocumentMapper userMapper;

    public Mono<UserModel> toModel(Mono<UserDocument> userMono) {
        return userMono.map(userMapper::userDocumentToUserModel);
    }

    public Mono<UserDocument> toDocument(Mono<UserModel> dtoMono) {
        return dtoMono.map(userMapper::userModelToUserDocument);
    }
}
