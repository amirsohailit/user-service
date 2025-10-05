package com.mytestorg.user.infrastructure.adapter.output.persistance.repository;

import com.mytestorg.user.infrastructure.adapter.output.persistance.entity.UserDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserReactiveMongoRepository extends ReactiveMongoRepository<UserDocument, String> {
    Mono<UserDocument> findByEmail(String email);

    Flux<UserDocument> findAllByCountry(String country, Pageable pageable);

    Mono<Long> countByCountry(String country);

    Flux<UserDocument> findAllBy(Pageable pageable);

}
