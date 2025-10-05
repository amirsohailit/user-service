package com.mytestorg.user.service;

import com.mytestorg.user.application.port.input.user.CreateUserQuery;
import com.mytestorg.user.application.port.input.user.CreateUserServicePort;
import com.mytestorg.user.application.port.ouput.MessageBrokerServicePort;
import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.adapter.output.constants.UserEventType;
import com.mytestorg.user.infrastructure.adapter.output.messaging.kafka.KafkaEventMessage;
import com.mytestorg.user.infrastructure.adapter.output.persistance.CreateUserReactivePersistenceAdapter;
import com.mytestorg.user.infrastructure.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateCreateUserService implements CreateUserServicePort {
    private final CreateUserReactivePersistenceAdapter createUserReactivePersistenceAdapter;
    private final MessageBrokerServicePort messageBrokerServicePort;
    private final JsonUtil jsonUtil;

    @Override
    public Mono<UserModel> createUser(CreateUserQuery createUserQuery) {
        return createUserReactivePersistenceAdapter.createUser(createUserQuery).
                doOnSuccess(savedUser -> Mono.fromRunnable(() -> messageBrokerServicePort
                                .publish(jsonUtil.toJson(KafkaEventMessage.documentToKafkaMessage(UserEventType.USER_CREATED, savedUser)))
                                .subscribe()).subscribeOn(Schedulers.boundedElastic())
                        .subscribe());
    }
}
