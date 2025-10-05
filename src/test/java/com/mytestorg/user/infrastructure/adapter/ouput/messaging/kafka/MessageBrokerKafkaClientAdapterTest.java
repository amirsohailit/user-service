package com.mytestorg.user.infrastructure.adapter.ouput.messaging.kafka;

import com.mytestorg.user.infrastructure.adapter.output.messaging.kafka.MessageBrokerKafkaClientAdapter;
import com.mytestorg.user.infrastructure.adapter.output.persistance.repository.UserNotificationEventReactiveMongoRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageBrokerKafkaClientAdapterTest {
    private final String testEvent = "{\n" +
            "  \"userEventType\": \"USER_CREATED\",\n" +
            "  \"userId\": \"6e1a314b-2f13-4c5e-8f7d-13dfc8e6a123\",\n" +
            "  \"firstName\": \"Test\",\n" +
            "  \"lastName\": \"Test\",\n" +
            "  \"nickname\": \"ally\",\n" +
            "  \"email\": \"Test.Test@example.com\",\n" +
            "  \"country\": \"US\",\n" +
            "  \"createdAt\": \"2025-07-27T14:23:00Z\",\n" +
            "  \"updatedAt\": \"2025-07-27T14:23:00Z\"\n" +
            "}";
    private final SendResult<String, String> mockSendResult = new SendResult<>(
            new ProducerRecord<>("test-topic", "test-key", testEvent),
            new RecordMetadata(new TopicPartition("topic", 0), 0, 0, 0L, 0, 0)
    );
    private final CompletableFuture<SendResult<String, String>> completedFuture =
            CompletableFuture.completedFuture(mockSendResult);
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private UserNotificationEventReactiveMongoRepository userNotificationEventReactiveMongoRepository;
    @InjectMocks
    private MessageBrokerKafkaClientAdapter messageBrokerKafkaClientAdapter;
    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(messageBrokerKafkaClientAdapter, "topic", "test-topic");
    }

    @Test
    void publish_WhenKafkaSendSucceeds_ShouldCompleteSuccessfully() {
        when(kafkaTemplate.send(anyString(), anyString(), eq(testEvent)))
                .thenReturn(completedFuture);
        StepVerifier.create(messageBrokerKafkaClientAdapter.publish(testEvent))
                .verifyComplete();

        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), eq(testEvent));
        verifyNoInteractions(userNotificationEventReactiveMongoRepository);
    }

    @Test
    void publish_WhenKafkaFailsThenSucceedsOnRetry_ShouldNotStoreInDb() {
        CompletableFuture<SendResult<String, String>> failed = new CompletableFuture<>();
        failed.completeExceptionally(new RuntimeException("Kafka error"));

        when(kafkaTemplate.send(anyString(), anyString(), eq(testEvent)))
                .thenReturn(failed)
                .thenReturn(failed)
                .thenReturn(completedFuture); // success on third attempt

        StepVerifier.create(messageBrokerKafkaClientAdapter.publish(testEvent))
                .verifyComplete();

        verify(kafkaTemplate, times(3)).send(anyString(), anyString(), eq(testEvent));
        verifyNoInteractions(userNotificationEventReactiveMongoRepository);
    }

    @Test
    void publish_WhenKafkaFailsAfterRetries_ShouldStoreInDb() {
        CompletableFuture<SendResult<String, String>> failed = new CompletableFuture<>();
        failed.completeExceptionally(new RuntimeException("Kafka error"));

        when(kafkaTemplate.send(anyString(), anyString(), eq(testEvent)))
                .thenReturn(failed);

        when(userNotificationEventReactiveMongoRepository.save(any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(messageBrokerKafkaClientAdapter.publish(testEvent))
                .verifyComplete();

        verify(kafkaTemplate, times(3)).send(anyString(), anyString(), eq(testEvent));
        verify(userNotificationEventReactiveMongoRepository, times(1)).save(any());
    }
}
