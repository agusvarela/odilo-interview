package com.odilo.interview.service;

import com.odilo.interview.api.dto.ChangePasswordRequest;
import com.odilo.interview.model.UserEntity;

import java.util.List;

public interface AdminService {

    List<UserEntity> getAllUsers();

    void changeUserPassword(Long userId, ChangePasswordRequest changePasswordRequest);

}
