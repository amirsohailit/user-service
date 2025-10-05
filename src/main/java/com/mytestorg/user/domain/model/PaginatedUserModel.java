package com.mytestorg.user.domain.model;

import java.util.List;

public record PaginatedUserModel(List<UserModel> users, long total, int page, int size) {
}
