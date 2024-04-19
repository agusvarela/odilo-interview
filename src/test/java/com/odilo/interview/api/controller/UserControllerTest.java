package com.odilo.interview.api.controller;

import com.odilo.interview.api.dto.UserResponse;
import com.odilo.interview.api.mapper.UserMapper;
import com.odilo.interview.model.UserEntity;
import com.odilo.interview.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService, userMapper);
    }

    @Test
    void getUserById_ReturnsUserWithCorrectId() {
        Long userId = 1L;
        String authorizationHeader = "Bearer token";

        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();

        when(userService.getUserById(userId, authorizationHeader)).thenReturn(userEntity);

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();

        when(userMapper.userToUserResponse(userEntity)).thenReturn(userResponse);

        ResponseEntity<UserResponse> responseEntity = userController.getUserById(userId, authorizationHeader);

        assertEquals(OK, responseEntity.getStatusCode());
        assertEquals(userResponse, responseEntity.getBody());

        verify(userService, times(1)).getUserById(userId, authorizationHeader);
        verify(userMapper, times(1)).userToUserResponse(userEntity);
    }

    @Test
    void deleteUserById_UserDeletedSuccessfully() {
        Long userId = 1L;
        String authorizationHeader = "Bearer token";

        ResponseEntity<?> responseEntity = userController.deleteUserById(userId, authorizationHeader);

        assertEquals(NO_CONTENT, responseEntity.getStatusCode());

        verify(userService, times(1)).deleteUserById(userId, authorizationHeader);
    }
}
