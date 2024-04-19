package com.odilo.interview.service.impl;

import com.odilo.interview.api.dto.ChangePasswordRequest;
import com.odilo.interview.exception.ApiException;
import com.odilo.interview.model.UserEntity;
import com.odilo.interview.repository.adapter.CacheUserRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AdminServiceImplTest {

    @InjectMocks
    private AdminServiceImpl adminService;

    @Mock
    private CacheUserRepositoryAdapter cacheUserRepositoryAdapter;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminService = new AdminServiceImpl(cacheUserRepositoryAdapter, passwordEncoder);
    }

    @Test
    void getAllUsers_ReturnsListOfUsers() {
        UserEntity user1 = buildUserEntity("user1");
        user1.setId(1L);

        UserEntity user2 = buildUserEntity("user2");
        user2.setId(2L);

        List<UserEntity> userEntityList = Arrays.asList(user1, user2);

        when(cacheUserRepositoryAdapter.findAll()).thenReturn(userEntityList);

        List<UserEntity> result = adminService.getAllUsers();

        assertEquals(userEntityList, result);

        verify(cacheUserRepositoryAdapter, times(1)).findAll();
    }

    @Test
    void changeUserPassword_PasswordChangedSuccessfully() {
        Long userId = 1L;
        String newPassword = "newPassword";
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(newPassword);
        UserEntity userEntity = buildUserEntity("user1");

        when(cacheUserRepositoryAdapter.findById(userId)).thenReturn(userEntity);

        adminService.changeUserPassword(userId, changePasswordRequest);

        verify(cacheUserRepositoryAdapter, times(1)).findById(userId);
    }

    @Test
    void changeUserPassword_SamePassword_ThrowsApiException() {
        Long userId = 1L;
        String newPassword = "oldPassword";
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(newPassword);
        UserEntity userEntity = buildUserEntity("user1");

        when(cacheUserRepositoryAdapter.findById(userId)).thenReturn(userEntity);
        when(passwordEncoder.matches(newPassword, userEntity.getPassword())).thenReturn(true);

        assertThrows(ApiException.class, () -> adminService.changeUserPassword(userId, changePasswordRequest));

        verify(cacheUserRepositoryAdapter, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches(newPassword, userEntity.getPassword());
    }

    private UserEntity buildUserEntity(String username) {
        return UserEntity.builder()
                .username(username)
                .password("oldPassword")
                .build();
    }
}
