package com.mytestorg.user.service;

import com.mytestorg.user.application.port.input.user.GetAllUserServicePort;
import com.mytestorg.user.domain.model.PaginatedUserModel;
import com.mytestorg.user.infrastructure.adapter.output.persistance.GetAllUserReactivePersistenceAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetAllUserService implements GetAllUserServicePort {
    private final GetAllUserReactivePersistenceAdapter getAllUserReactivePersistenceAdapter;

    @Override
    public Mono<PaginatedUserModel> getUsers(String country, int page, int size) {
        return getAllUserReactivePersistenceAdapter.getUsers(country, page, size);

    }
}
