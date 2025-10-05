package com.mytestorg.user.service;

import com.mytestorg.user.application.port.input.user.DeleteUserServicePort;
import com.mytestorg.user.application.port.ouput.MessageBrokerServicePort;
import com.mytestorg.user.infrastructure.adapter.output.constants.UserEventType;
import com.mytestorg.user.infrastructure.adapter.output.messaging.kafka.KafkaEventMessage;
import com.mytestorg.user.infrastructure.adapter.output.persistance.DeleteUserReactivePersistenceAdapter;
import com.mytestorg.user.infrastructure.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteUserService implements DeleteUserServicePort {
    final MessageBrokerServicePort messageBrokerServicePort;
    private final DeleteUserReactivePersistenceAdapter deleteUserReactivePersistenceAdapter;
    private final JsonUtil jsonUtil;

    @Override
    public Mono<Void> deleteUser(String userId) {
        return deleteUserReactivePersistenceAdapter.deleteUser(userId)
                .doOnSuccess(savedUser -> Mono.fromRunnable(() -> messageBrokerServicePort
                                .publish(jsonUtil.toJson(KafkaEventMessage.documentToKafkaMessage(UserEventType.USER_DELETED, savedUser)))
                                .subscribe()).subscribeOn(Schedulers.boundedElastic())
                        .subscribe()).then();
    }
}
