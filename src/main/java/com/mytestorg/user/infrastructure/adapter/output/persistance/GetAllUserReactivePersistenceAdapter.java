package com.mytestorg.user.infrastructure.adapter.output.persistance;

import com.mytestorg.user.application.port.ouput.GetAllUserPort;
import com.mytestorg.user.domain.model.PaginatedUserModel;
import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.adapter.output.persistance.entity.UserDocument;
import com.mytestorg.user.infrastructure.adapter.output.persistance.mapper.UserModelDocumentMapper;
import com.mytestorg.user.infrastructure.adapter.output.persistance.repository.UserReactiveMongoRepository;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class GetAllUserReactivePersistenceAdapter implements GetAllUserPort {
    private final UserModelDocumentMapper userModelDocumentMapper;
    private final UserReactiveMongoRepository userReactiveMongoRepository;

    @Override
    public Mono<PaginatedUserModel> getUsers(String country, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (StringUtils.isNotBlank(country)) {
            Mono<List<UserModel>> usersListMono = userReactiveMongoRepository.findAllByCountry(country, pageable).map(userModelDocumentMapper::userDocumentToUserModel).collectList();

            Mono<Long> countMono = userReactiveMongoRepository.countByCountry(country);

            return usersListMono.zipWith(countMono).map(tuple -> new PaginatedUserModel(tuple.getT1(), tuple.getT2(), page, size));
        } else {
            Mono<List<UserDocument>> usersListMono = userReactiveMongoRepository.findAllBy(pageable).collectList();
            Mono<Long> countMono = userReactiveMongoRepository.count();

            return usersListMono.map(list -> list.stream().map(userModelDocumentMapper::userDocumentToUserModel).toList()).zipWith(countMono).map(tuple -> new PaginatedUserModel(tuple.getT1(), tuple.getT2(), page, size));
        }
    }

}
