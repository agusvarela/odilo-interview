package com.odilo.interview.service.impl;

import com.odilo.interview.api.dto.LoginRequest;
import com.odilo.interview.api.dto.LoginResponse;
import com.odilo.interview.api.dto.RegisterRequest;
import com.odilo.interview.exception.ApiException;
import com.odilo.interview.auth.AuthJWT;
import com.odilo.interview.kafka.KafkaProducer;
import com.odilo.interview.model.Role;
import com.odilo.interview.model.UserEntity;
import com.odilo.interview.repository.RoleRepository;
import com.odilo.interview.repository.UserRepository;
import com.odilo.interview.repository.adapter.CacheUserRepositoryAdapter;
import com.odilo.interview.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    public static final String ROLE_USER = "ROLE_USER";

    private final AuthJWT authJwt;
    private final KafkaProducer kafkaProducer;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CacheUserRepositoryAdapter cacheUserRepositoryAdapter;

    @Override
    public UserEntity register(RegisterRequest registerRequest) {
        log.info("Registering new user: {}", registerRequest.getUsername());

        int years = Period.between(registerRequest.getDateOfBirth(), LocalDate.now()).getYears();
        if (years < 18) {
            throw new ApiException("The user is under 18.", BAD_REQUEST);
        }

        if (userRepository.existsByUsernameOrEmail(registerRequest.getUsername(),
                registerRequest.getEmail().toLowerCase())) {

            throw new ApiException("Username or email already exists: " + registerRequest.getUsername(), CONFLICT);
        }

        Role userRole = roleRepository.findByName(ROLE_USER)
                .orElseThrow(() -> new ApiException("Role not found: " + ROLE_USER, NOT_FOUND));

        UserEntity newUserEntity = UserEntity.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .dateOfBirth(registerRequest.getDateOfBirth())
                .roles(List.of(userRole))
                .build();

        UserEntity userSaved = cacheUserRepositoryAdapter.save(newUserEntity);
        kafkaProducer.sendMessage("register",
                "There is a new user with username: " + userSaved.getUsername());

        return userSaved;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Logging in user: {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        LoginResponse loginResponse = LoginResponse.builder()
                .jwtToken(authJwt.generateJWTToken(authentication))
                .build();

        kafkaProducer.sendMessage("login", "New user logged. Username: " + loginRequest.getUsername());
        return loginResponse;
    }
}
