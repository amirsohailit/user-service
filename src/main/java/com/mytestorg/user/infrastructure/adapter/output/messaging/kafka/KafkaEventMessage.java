package com.mytestorg.user.infrastructure.adapter.output.messaging.kafka;

import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.adapter.output.constants.UserEventType;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class KafkaEventMessage {
    private UserEventType userEventType;
    private String userId;
    private String firstName;
    private String lastName;
    private String nickname;
    private String email;
    private String country;
    private Instant createdAt;
    private Instant updatedAt;

    public static KafkaEventMessage documentToKafkaMessage(UserEventType userEventType, UserModel userModel) {
        return KafkaEventMessage.builder()
                .userEventType(userEventType)
                .userId(userModel.id())
                .firstName(userModel.firstName())
                .lastName(userModel.lastName())
                .nickname(userModel.nickname())
                .email(userModel.email())
                .country(userModel.country())
                .createdAt(userModel.createdAt().toInstant())
                .updatedAt(userModel.updatedAt() != null ? userModel.updatedAt().toInstant() : null)
                .build();
    }
}
