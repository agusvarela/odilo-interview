package com.odilo.interview.api.controller;

import com.odilo.interview.api.dto.ChangePasswordRequest;
import com.odilo.interview.api.dto.UserListResponse;
import com.odilo.interview.api.dto.UserResponse;
import com.odilo.interview.api.mapper.UserMapper;
import com.odilo.interview.model.Role;
import com.odilo.interview.model.UserEntity;
import com.odilo.interview.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private AdminService adminService;

    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminController = new AdminController(adminService, userMapper);
    }

    @Test
    void getAllUsers_ReturnsListOfUsers() {
        List<UserEntity> userEntityList = Arrays.asList(
                new UserEntity(1L, "user1", "pass", "user1@example.com",
                        LocalDate.of(1990, 1, 1), List.of(buildUserRole())),
                new UserEntity(2L, "user2", "pass", "user2@example.com",
                        LocalDate.of(1995, 5, 5), List.of(buildUserRole()))
        );
        when(adminService.getAllUsers()).thenReturn(userEntityList);

        List<UserResponse> userResponseList = Arrays.asList(
                new UserResponse(1L, "user1", "user1@example.com", LocalDate.of(1990, 1, 1)),
                new UserResponse(2L, "user2", "user2@example.com", LocalDate.of(1995, 5, 5))
        );
        when(userMapper.userListToUserListResponse(any())).thenReturn(new UserListResponse(userResponseList));

        ResponseEntity<UserListResponse> responseEntity = adminController.getAllUsers();

        assertEquals(OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(userResponseList, responseEntity.getBody().getUsers());

        verify(adminService, times(1)).getAllUsers();
        verify(userMapper, times(1)).userListToUserListResponse(userEntityList);
    }

    @Test
    void changeUserPassword_PasswordChangedSuccessfully() {
        Long userId = 1L;
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("newPassword");

        ResponseEntity<?> responseEntity = adminController.changeUserPassword(userId, changePasswordRequest);

        assertEquals(NO_CONTENT, responseEntity.getStatusCode());

        verify(adminService, times(1)).changeUserPassword(userId, changePasswordRequest);
    }

    private Role buildUserRole() {
        return Role.builder()
                .id(1L)
                .name("ROLE_USER")
                .build();
    }
}
