package com.mytestorg.user.infrastructure.adapter.input.rest.mapper;

import com.mytestorg.user.domain.model.PaginatedUserModel;
import com.mytestorg.user.infrastructure.adapter.input.rest.dto.response.PaginatedUserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaginatedUserMapper {
    PaginatedUserResponse userModelToUserResponse(final PaginatedUserModel paginatedUserModel);
}
