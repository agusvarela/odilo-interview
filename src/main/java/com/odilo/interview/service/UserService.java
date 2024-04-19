package com.odilo.interview.service;

import com.odilo.interview.model.UserEntity;

public interface UserService {

    UserEntity getUserById(Long userId, String authorizationHeader);

    void deleteUserById(Long userId, String authorizationHeader);

}
