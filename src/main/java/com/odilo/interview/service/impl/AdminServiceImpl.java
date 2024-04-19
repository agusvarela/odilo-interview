package com.odilo.interview.service.impl;

import com.odilo.interview.api.dto.ChangePasswordRequest;
import com.odilo.interview.exception.ApiException;
import com.odilo.interview.model.UserEntity;
import com.odilo.interview.repository.adapter.CacheUserRepositoryAdapter;
import com.odilo.interview.service.AdminService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;

@Slf4j
@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CacheUserRepositoryAdapter cacheUserRepositoryAdapter;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserEntity> getAllUsers() {
        log.info("Retrieving all users...");
        return cacheUserRepositoryAdapter.findAll();
    }

    @Override
    @Transactional
    public void changeUserPassword(Long userId, ChangePasswordRequest changePasswordRequest) {
        log.info("Changing password for user with id: {}", userId);

        UserEntity userEntity = cacheUserRepositoryAdapter.findById(userId);

        if (passwordEncoder.matches(changePasswordRequest.getPassword(), userEntity.getPassword())) {
            throw new ApiException("New and actual password are the same.", CONFLICT);
        }

        userEntity.setPassword(passwordEncoder.encode(changePasswordRequest.getPassword()));
        cacheUserRepositoryAdapter.save(userEntity);
    }
}
