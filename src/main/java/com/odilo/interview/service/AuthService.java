package com.odilo.interview.service;

import com.odilo.interview.api.dto.LoginRequest;
import com.odilo.interview.api.dto.LoginResponse;
import com.odilo.interview.api.dto.RegisterRequest;
import com.odilo.interview.model.UserEntity;

public interface AuthService {

    UserEntity register(RegisterRequest registerRequest);

    LoginResponse login(LoginRequest loginRequest);

}
