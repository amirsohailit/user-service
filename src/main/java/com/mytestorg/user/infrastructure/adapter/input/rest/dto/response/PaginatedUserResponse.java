package com.mytestorg.user.infrastructure.adapter.input.rest.dto.response;

import com.mytestorg.user.domain.model.UserModel;

import java.util.List;

public record PaginatedUserResponse(List<UserModel> users, long total, int page, int size) {
}
