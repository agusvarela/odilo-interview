package com.odilo.interview.service.impl;

import com.odilo.interview.auth.AuthJWT;
import com.odilo.interview.exception.ApiException;
import com.odilo.interview.kafka.KafkaProducer;
import com.odilo.interview.model.UserEntity;
import com.odilo.interview.repository.adapter.CacheUserRepositoryAdapter;
import com.odilo.interview.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthJWT authJWT;
    private final KafkaProducer kafkaProducer;
    private final CacheUserRepositoryAdapter cacheUserRepositoryAdapter;

    @Override
    public UserEntity getUserById(Long userId, String authorizationHeader) {
        log.info("Retrieving user by id: {}", userId);
        return getUserByIdAndCheckByUsername(userId, authorizationHeader);
    }

    @Override
    public void deleteUserById(Long userId, String authorizationHeader) {
        log.info("Deleting user by id: {}", userId);
        UserEntity userSaved = getUserByIdAndCheckByUsername(userId, authorizationHeader);
        cacheUserRepositoryAdapter.deleteUser(userSaved);
        kafkaProducer.sendMessage("userDeleted", "The user [" + userSaved.getUsername() + "] was deleted.");
    }

    private UserEntity getUserByIdAndCheckByUsername(Long userId, String authorizationHeader) {
        UserEntity user = cacheUserRepositoryAdapter.findById(userId);
        String usernameFromJWT = authJWT.getUsernameFromToken(authorizationHeader.substring(7));

        if (!user.getUsername().equalsIgnoreCase(usernameFromJWT)) {
            throw new ApiException("User id does not match with the username logged.", FORBIDDEN);
        }
        return user;
    }
}
