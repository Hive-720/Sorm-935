package com.grunvald.sorm.service.whmcsdata.datasyncservice.user;

import com.grunvald.sorm.service.whmcsdata.dataservice.user.UserService;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.model.User;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.model.UserClientRelation;
import com.grunvald.sorm.service.whmcsprovider.WhmcsProvider;
import com.grunvald.sorm.service.whmcsprovider.dto.UserProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.UserMapper;
import com.grunvald.sorm.service.whmcsprovider.result.UsersResult;
import com.grunvald.sorm.util.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSyncServiceImplTest {

    private static final OffsetDateTime NOW = OffsetDateTime.parse("2026-01-01T10:00:00+03:00");

    @Mock
    private UserService userService;
    @Mock
    private WhmcsProvider whmcs;
    @Mock
    private UserMapper userMapper;
    @Mock
    private DateUtils date;

    private UserSyncServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserSyncServiceImpl(userService, whmcs, userMapper, date);
    }

    private UserProviderDto userDto(long id, UserProviderDto.Client... clients) {
        var dto = new UserProviderDto();
        dto.setId(id);
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setClients(List.of(clients));
        return dto;
    }

    private UserProviderDto.Client client(long id) {
        return new UserProviderDto.Client(id);
    }

    @Test
    void getActualData_mapsUsersAndBuildsRelations() {
        var dto = userDto(1L, client(10L), client(11L));
        var user = new User();
        user.setId(1L);
        when(whmcs.getUsers(0, 100)).thenReturn(Mono.just(UsersResult.builder()
                .elements(List.of(dto))
                .hashNextPage(false)
                .nextOffset(0L)
                .build()));
        when(userMapper.map(dto)).thenReturn(user);
        when(date.now()).thenReturn(NOW);

        StepVerifier.create(service.getActualData(0, 100))
                .assertNext(page -> {
                    assertEquals(List.of(user), page.getUsers());
                    var relations = page.getRelations();
                    assertEquals(2, relations.size());
                    assertEquals(1L, relations.get(0).getUserId());
                    assertEquals(10L, relations.get(0).getClientId());
                    assertEquals(NOW, relations.get(0).getCreatedAt());
                    assertEquals(1L, relations.get(1).getUserId());
                    assertEquals(11L, relations.get(1).getClientId());
                    assertEquals(NOW, relations.get(1).getCreatedAt());
                })
                .verifyComplete();
    }

    @Test
    void getActualData_userWithoutClients_hasNoRelations() {
        var dto = userDto(1L);
        var user = new User();
        user.setId(1L);
        when(whmcs.getUsers(0, 100)).thenReturn(Mono.just(UsersResult.builder()
                .elements(List.of(dto))
                .hashNextPage(false)
                .nextOffset(0L)
                .build()));
        when(userMapper.map(dto)).thenReturn(user);

        StepVerifier.create(service.getActualData(0, 100))
                .assertNext(page -> {
                    assertEquals(List.of(user), page.getUsers());
                    assertTrue(page.getRelations().isEmpty());
                })
                .verifyComplete();
    }

    @Test
    void getActualData_emptyElements_producesEmptyPage() {
        when(whmcs.getUsers(0, 100)).thenReturn(Mono.just(UsersResult.builder()
                .elements(List.of())
                .hashNextPage(false)
                .nextOffset(0L)
                .build()));

        StepVerifier.create(service.getActualData(0, 100))
                .assertNext(page -> {
                    assertTrue(page.getUsers().isEmpty());
                    assertTrue(page.getRelations().isEmpty());
                })
                .verifyComplete();
    }

    @Test
    void applyActualData_savesUsersThenRelations() {
        var user = new User();
        user.setId(1L);
        var relation = new UserClientRelation(1L, 10L, NOW);
        var page = UserSyncPage.builder()
                .users(List.of(user))
                .relations(List.of(relation))
                .build();
        when(userService.saveOrUpdateAll(List.of(user))).thenReturn(Mono.just(true));
        when(userService.saveOrRemoveRelationsAll(List.of(relation))).thenReturn(Mono.just(true));

        StepVerifier.create(service.applyActualData(page))
                .expectNext(true)
                .verifyComplete();

        verify(userService).saveOrUpdateAll(List.of(user));
        verify(userService).saveOrRemoveRelationsAll(List.of(relation));
    }
}
