package com.mytestorg.user.service;

import com.mytestorg.user.application.port.input.user.GetUserServicePort;
import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.adapter.output.persistance.GetUserReactivePersistenceAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetUserService implements GetUserServicePort {
    private final GetUserReactivePersistenceAdapter getUserReactivePersistenceAdapter;

    @Override
    public Mono<UserModel> getUser(String id) {
        return getUserReactivePersistenceAdapter.getUser(id);

    }
}
