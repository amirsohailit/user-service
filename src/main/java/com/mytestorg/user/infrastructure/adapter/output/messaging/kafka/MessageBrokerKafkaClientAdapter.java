package com.mytestorg.user.infrastructure.adapter.output.messaging.kafka;

import com.mytestorg.user.application.port.ouput.MessageBrokerServicePort;
import com.mytestorg.user.infrastructure.adapter.output.constants.EventNotificationStatus;
import com.mytestorg.user.infrastructure.adapter.output.persistance.entity.UserNotificationEventDocument;
import com.mytestorg.user.infrastructure.adapter.output.persistance.repository.UserNotificationEventReactiveMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageBrokerKafkaClientAdapter implements MessageBrokerServicePort {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserNotificationEventReactiveMongoRepository userNotificationEventReactiveMongoRepository;
    @Value("${spring.kafka.topic}")
    private String topic;
    @Override
    public Mono<Void> publish(String event) {
        String key = UUID.randomUUID().toString();
        return Mono.defer(() -> Mono.fromCompletionStage(kafkaTemplate.send(topic, key, event)))
                .doOnSuccess(result -> log.info("Successfully published event {}", event))
                .doOnError(e -> log.error("Failed to publish Kafka event"))
                .retry(2)
                .onErrorResume(e -> {
                    log.error("All retries failed. Event not published.", e);
                    return userNotificationEventReactiveMongoRepository.save(
                            UserNotificationEventDocument.builder()
                                    .id(key)
                                    .message(event)
                                    .notificationStatus(EventNotificationStatus.FAILED)
                                    .timestamp(Instant.now())
                                    .build()
                    ).then(Mono.empty());

                })
                .then();
    }
}
