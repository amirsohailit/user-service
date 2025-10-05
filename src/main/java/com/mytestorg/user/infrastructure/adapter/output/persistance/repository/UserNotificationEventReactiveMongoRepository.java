package com.mytestorg.user.infrastructure.adapter.output.persistance.repository;

import com.mytestorg.user.infrastructure.adapter.output.persistance.entity.UserNotificationEventDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNotificationEventReactiveMongoRepository extends ReactiveMongoRepository<UserNotificationEventDocument, String> {


}
