package com.odilo.interview.service.impl;

import com.odilo.interview.auth.AuthJWT;
import com.odilo.interview.exception.ApiException;
import com.odilo.interview.kafka.KafkaProducer;
import com.odilo.interview.model.UserEntity;
import com.odilo.interview.repository.adapter.CacheUserRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private AuthJWT authJWT;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private CacheUserRepositoryAdapter cacheUserRepositoryAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(authJWT, kafkaProducer, cacheUserRepositoryAdapter);
    }

    @Test
    void getUserById_ReturnsUserWithCorrectId() {
        Long userId = 1L;
        String authorizationHeader = "Bearer token";
        UserEntity userEntity = buildUserEntity();

        when(cacheUserRepositoryAdapter.findById(userId)).thenReturn(userEntity);
        when(authJWT.getUsernameFromToken("token")).thenReturn("user1");

        UserEntity result = userService.getUserById(userId, authorizationHeader);

        assertEquals(userEntity, result);
        verify(cacheUserRepositoryAdapter, times(1)).findById(userId);
        verify(authJWT, times(1)).getUsernameFromToken("token");
    }

    @Test
    void getUserById_UnauthorizedAccess_ThrowsApiException() {
        Long userId = 1L;
        String authorizationHeader = "Bearer token";
        UserEntity userEntity = buildUserEntity();

        when(cacheUserRepositoryAdapter.findById(userId)).thenReturn(userEntity);
        when(authJWT.getUsernameFromToken("token")).thenReturn("badUsername");

        assertThrows(ApiException.class, () -> userService.getUserById(userId, authorizationHeader));

        verify(cacheUserRepositoryAdapter, times(1)).findById(userId);
        verify(authJWT, times(1)).getUsernameFromToken("token");
    }

    @Test
    void deleteUserById_UserDeletedSuccessfully() {
        Long userId = 1L;
        String authorizationHeader = "Bearer token";
        UserEntity userEntity = buildUserEntity();

        when(cacheUserRepositoryAdapter.findById(userId)).thenReturn(userEntity);
        when(authJWT.getUsernameFromToken("token")).thenReturn("user1");

        userService.deleteUserById(userId, authorizationHeader);

        verify(cacheUserRepositoryAdapter, times(1)).findById(userId);
        verify(authJWT, times(1)).getUsernameFromToken("token");
        verify(cacheUserRepositoryAdapter, times(1)).deleteUser(userEntity);
    }

    @Test
    void deleteUserById_UnauthorizedAccess_ThrowsApiException() {
        Long userId = 1L;
        String authorizationHeader = "Bearer token";
        UserEntity userEntity = buildUserEntity();

        when(cacheUserRepositoryAdapter.findById(userId)).thenReturn(userEntity);
        when(authJWT.getUsernameFromToken("token")).thenReturn("badUsername");

        assertThrows(ApiException.class, () -> userService.deleteUserById(userId, authorizationHeader));

        verify(cacheUserRepositoryAdapter, times(1)).findById(userId);
        verify(authJWT, times(1)).getUsernameFromToken("token");
    }

    private UserEntity buildUserEntity() {
        return UserEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();
    }
}
