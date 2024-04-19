package com.odilo.interview.api.controller;

import com.odilo.interview.api.dto.LoginRequest;
import com.odilo.interview.api.dto.LoginResponse;
import com.odilo.interview.api.dto.RegisterRequest;
import com.odilo.interview.api.dto.UserResponse;
import com.odilo.interview.api.mapper.UserMapper;
import com.odilo.interview.model.UserEntity;
import com.odilo.interview.service.AuthService;
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
import static org.springframework.http.HttpStatus.OK;

public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(userMapper, authService);
    }

    @Test
    void register_NewUserRegisteredSuccessfully() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("user1")
                .email("user1@example.com")
                .password("password")
                .build();

        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();

        when(authService.register(registerRequest)).thenReturn(userEntity);

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();

        when(userMapper.userToUserResponse(userEntity)).thenReturn(userResponse);

        ResponseEntity<UserResponse> responseEntity = authController.register(registerRequest);

        assertEquals(OK, responseEntity.getStatusCode());
        assertEquals(userResponse, responseEntity.getBody());


        verify(authService, times(1)).register(registerRequest);
        verify(userMapper, times(1)).userToUserResponse(userEntity);
    }

    @Test
    void login_UserLoggedInSuccessfully() {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("user1")
                .password("password")
                .build();

        LoginResponse loginResponse = LoginResponse.builder()
                .jwtToken("token")
                .build();

        when(authService.login(loginRequest)).thenReturn(loginResponse);

        ResponseEntity<LoginResponse> responseEntity = authController.login(loginRequest);

        assertEquals(OK, responseEntity.getStatusCode());
        assertEquals(loginResponse, responseEntity.getBody());

        verify(authService, times(1)).login(loginRequest);
    }
}
