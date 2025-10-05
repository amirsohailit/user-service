package com.mytestorg.user.infrastructure.adapter.input.rest.mapper;

import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.adapter.input.rest.dto.response.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse userModelToUserResponse(final UserModel crewMemberInfo);
}
