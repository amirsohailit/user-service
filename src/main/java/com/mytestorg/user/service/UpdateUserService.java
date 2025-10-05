package com.mytestorg.user.service;

import com.mytestorg.user.application.port.input.user.UpdateUserQuery;
import com.mytestorg.user.application.port.input.user.UpdateUserServicePort;
import com.mytestorg.user.application.port.ouput.MessageBrokerServicePort;
import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.adapter.output.constants.UserEventType;
import com.mytestorg.user.infrastructure.adapter.output.messaging.kafka.KafkaEventMessage;
import com.mytestorg.user.infrastructure.adapter.output.persistance.UpdateUserReactivePersistenceAdapter;
import com.mytestorg.user.infrastructure.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateUserService implements UpdateUserServicePort {
    final MessageBrokerServicePort messageBrokerServicePort;
    private final UpdateUserReactivePersistenceAdapter updateUserReactivePersistenceAdapter;
    private final JsonUtil jsonUtil;

    @Override
    public Mono<UserModel> updateUser(String id, UpdateUserQuery updateUserQuery) {
        return updateUserReactivePersistenceAdapter.updateUser(id, updateUserQuery)
                .doOnSuccess(savedUser -> Mono.fromRunnable(() -> messageBrokerServicePort
                                .publish(jsonUtil.toJson(KafkaEventMessage.documentToKafkaMessage(UserEventType.USER_UPDATED, savedUser)))
                                .subscribe()).subscribeOn(Schedulers.boundedElastic())
                        .subscribe());

    }
}
