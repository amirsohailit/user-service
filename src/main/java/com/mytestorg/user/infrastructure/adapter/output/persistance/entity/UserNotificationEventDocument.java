package com.mytestorg.user.infrastructure.adapter.output.persistance.entity;

import com.mytestorg.user.infrastructure.adapter.output.constants.EventNotificationStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Document(collection = "user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotificationEventDocument {
    @Id
    private String id;
    private String message;
    private EventNotificationStatus notificationStatus;
    private Instant timestamp;
}
