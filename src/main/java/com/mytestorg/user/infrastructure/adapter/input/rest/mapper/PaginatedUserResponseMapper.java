package com.mytestorg.user.infrastructure.adapter.input.rest.mapper;

import com.mytestorg.user.domain.model.PaginatedUserModel;
import com.mytestorg.user.infrastructure.adapter.input.rest.dto.response.PaginatedUserResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class PaginatedUserResponseMapper {
    private final PaginatedUserMapper paginatedUserMapper;

    public Mono<PaginatedUserResponse> modelToResponse(Mono<PaginatedUserModel> userModelMono) {
        return userModelMono.map(paginatedUserMapper::userModelToUserResponse);
    }
}
