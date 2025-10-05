package com.mytestorg.user.infrastructure.adapter.input.rest.mapper;

import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.adapter.input.rest.dto.response.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class UserResponseMapper {
    private final UserMapper userMapper;

    public Mono<UserResponse> modelToResponse(Mono<UserModel> userMono) {
        return userMono.map(userMapper::userModelToUserResponse);
    }
}
