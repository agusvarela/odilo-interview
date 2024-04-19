package com.odilo.interview.service.impl;

import com.odilo.interview.api.dto.LoginRequest;
import com.odilo.interview.api.dto.LoginResponse;
import com.odilo.interview.api.dto.RegisterRequest;
import com.odilo.interview.auth.AuthJWT;
import com.odilo.interview.exception.ApiException;
import com.odilo.interview.kafka.KafkaProducer;
import com.odilo.interview.model.Role;
import com.odilo.interview.model.UserEntity;
import com.odilo.interview.repository.RoleRepository;
import com.odilo.interview.repository.UserRepository;
import com.odilo.interview.repository.adapter.CacheUserRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class AuthServiceImplTest {


    private AuthServiceImpl authService;

    @Mock
    private AuthJWT authJwt;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CacheUserRepositoryAdapter cacheUserRepositoryAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(authJwt, kafkaProducer, userRepository, roleRepository, passwordEncoder,
                authenticationManager, cacheUserRepositoryAdapter);
    }

    @Test
    void register_NewUserRegisteredSuccessfully() {
        RegisterRequest registerRequest = buildRegisterRequest();

        Role userRole = Role.builder()
                .id(1L)
                .name("ROLE_USER")
                .build();

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(userRepository.existsByUsernameOrEmail("user1", "user1@example.com")).thenReturn(false);

        UserEntity newUserEntity = UserEntity.builder()
                .id(1L)
                .username("user1")
                .password("password")
                .email("user1@example.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .roles(Collections.singletonList(userRole))
                .build();

        when(cacheUserRepositoryAdapter.save(any(UserEntity.class))).thenReturn(newUserEntity);

        UserEntity result = authService.register(registerRequest);

        assertEquals(newUserEntity, result);

        verify(roleRepository, times(1)).findByName("ROLE_USER");
        verify(userRepository, times(1)).existsByUsernameOrEmail("user1", "user1@example.com");
        verify(cacheUserRepositoryAdapter, times(1)).save(any(UserEntity.class));
    }

    @Test
    void register_UserUnder18_ThrowsApiException() {
        RegisterRequest registerRequest = buildRegisterRequest();
        registerRequest.setDateOfBirth(LocalDate.now().minusYears(17));

        assertThrows(ApiException.class, () -> authService.register(registerRequest));

        verifyNoInteractions(roleRepository);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(cacheUserRepositoryAdapter);
    }

    @Test
    void register_ExistingUsernameOrEmail_ThrowsApiException() {
        RegisterRequest registerRequest = buildRegisterRequest();

        when(userRepository.existsByUsernameOrEmail("user1", "user1@example.com")).thenReturn(true);

        assertThrows(ApiException.class, () -> authService.register(registerRequest));

        verifyNoInteractions(roleRepository);
        verifyNoInteractions(cacheUserRepositoryAdapter);
        verify(userRepository, times(1)).existsByUsernameOrEmail("user1", "user1@example.com");
    }

    @Test
    void login_UserLoggedInSuccessfully() {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("user1")
                .password("password")
                .build();

        Authentication authentication = mock(Authentication.class);
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword());

        when(authenticationManager.authenticate(authRequest)).thenReturn(authentication);
        when(authJwt.generateJWTToken(authentication)).thenReturn("jwtToken");

        LoginResponse result = authService.login(loginRequest);

        assertEquals("jwtToken", result.getJwtToken());

        verify(authenticationManager, times(1)).authenticate(authRequest);
        verify(authJwt, times(1)).generateJWTToken(authentication);
    }

    private RegisterRequest buildRegisterRequest() {
        return RegisterRequest.builder()
                .username("user1")
                .email("user1@example.com")
                .password("password")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
    }
}
