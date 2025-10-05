package com.mytestorg.user.infrastructure.adapter.ouput.persistance;

import com.mytestorg.user.domain.model.UserModel;
import com.mytestorg.user.infrastructure.adapter.output.persistance.GetAllUserReactivePersistenceAdapter;
import com.mytestorg.user.infrastructure.adapter.output.persistance.entity.UserDocument;
import com.mytestorg.user.infrastructure.adapter.output.persistance.mapper.UserModelDocumentMapper;
import com.mytestorg.user.infrastructure.adapter.output.persistance.repository.UserReactiveMongoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllUserReactivePersistenceAdapterTest {

    @Mock
    private UserModelDocumentMapper userModelDocumentMapper;
    @Mock
    private UserReactiveMongoRepository userReactiveMongoRepository;

    @InjectMocks
    private GetAllUserReactivePersistenceAdapter adapter;

    private UserDocument buildUserDocument(String id, String country) {
        return UserDocument.builder()
                .id(id)
                .firstName("Alice")
                .lastName("Smith")
                .nickname("alices")
                .email("alice@example.com")
                .country(country)
                .createdAt(OffsetDateTime.now().toInstant())
                .build();
    }

    private UserModel buildUserModel(String id, String country) {
        return new UserModel(
                id, "Alice", "Smith", "alices",
                "alice@example.com", country,
                OffsetDateTime.now(), null
        );
    }

    @Test
    void getUsers_withCountry_shouldReturnPaginatedUserModel() {
        String country = "US";
        int page = 2;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);

        UserDocument userDoc = buildUserDocument("1", country);
        UserModel userModel = buildUserModel("1", country);

        when(userReactiveMongoRepository.findAllByCountry(eq(country), eq(pageable)))
                .thenReturn(Flux.just(userDoc));
        when(userModelDocumentMapper.userDocumentToUserModel(userDoc))
                .thenReturn(userModel);
        when(userReactiveMongoRepository.countByCountry(eq(country)))
                .thenReturn(Mono.just(1L));

        StepVerifier.create(adapter.getUsers(country, page, size))
                .expectNextMatches(paginated -> {
                    return paginated.users().size() == 1
                            && paginated.users().get(0).id().equals("1")
                            && paginated.total() == 1L
                            && paginated.page() == page
                            && paginated.size() == size;
                })
                .verifyComplete();

        verify(userReactiveMongoRepository).findAllByCountry(eq(country), eq(pageable));
        verify(userModelDocumentMapper).userDocumentToUserModel(userDoc);
        verify(userReactiveMongoRepository).countByCountry(eq(country));
    }

    @Test
    void getUsers_withoutCountry_shouldReturnPaginatedUserModel() {
        String country = null;
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        UserDocument userDoc1 = buildUserDocument("1", "US");
        UserDocument userDoc2 = buildUserDocument("2", "UK");
        UserModel userModel1 = buildUserModel("1", "US");
        UserModel userModel2 = buildUserModel("2", "UK");

        when(userReactiveMongoRepository.findAllBy(eq(pageable)))
                .thenReturn(Flux.just(userDoc1, userDoc2));
        when(userReactiveMongoRepository.count())
                .thenReturn(Mono.just(2L));
        when(userModelDocumentMapper.userDocumentToUserModel(userDoc1)).thenReturn(userModel1);
        when(userModelDocumentMapper.userDocumentToUserModel(userDoc2)).thenReturn(userModel2);
        when(userModelDocumentMapper.userDocumentToUserModel(userDoc2)).thenReturn(userModel2);

        StepVerifier.create(adapter.getUsers(country, page, size))
                .expectNextMatches(paginated -> {
                    return paginated.users().size() == 2
                            && paginated.total() == 2L
                            && paginated.users().get(0).id().equals("1")
                            && paginated.users().get(1).id().equals("2")
                            && paginated.page() == page
                            && paginated.size() == size;
                })
                .verifyComplete();

        verify(userReactiveMongoRepository).findAllBy(eq(pageable));
        verify(userReactiveMongoRepository).count();
        verify(userModelDocumentMapper).userDocumentToUserModel(userDoc1);
        verify(userModelDocumentMapper).userDocumentToUserModel(userDoc2);
    }
}
