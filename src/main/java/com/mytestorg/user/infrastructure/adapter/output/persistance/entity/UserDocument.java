package com.mytestorg.user.infrastructure.adapter.output.persistance.entity;

import jakarta.validation.constraints.Email;
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
public class UserDocument {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String nickname;
    private String password;
    @Email
    private String email;
    private String country;
    private Instant createdAt;
    private Instant updatedAt;
}
