package com.odilo.interview.repository.adapter;

import com.odilo.interview.exception.ApiException;
import com.odilo.interview.model.UserEntity;
import com.odilo.interview.repository.UserRepository;
import lombok.AllArgsConstructor;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
@AllArgsConstructor
@SuppressWarnings("unchecked")
public class CacheUserRepositoryAdapter {

    public static final String USERS_KEY = "users";
    public static final String ID_BY_USERNAME = "idByUsername";

    private final UserRepository userRepository;
    private final CacheManager cacheManager;
    private final RedisTemplate<String, UserEntity> userRedisTemplate;

    public UserEntity save(UserEntity user) {
        UserEntity userSaved = userRepository.save(user);
        userRedisTemplate.opsForValue().set(userSaved.getId().toString(), userSaved);
        Objects.requireNonNull(cacheManager.getCache(ID_BY_USERNAME)).put(userSaved.getUsername(), userSaved.getId());
        return userSaved;
    }

    public UserEntity findById(Long userId) {
        UserEntity user = userRedisTemplate.opsForValue().get(userId.toString());
        if (user == null) {
            user = userRepository.findById(userId).orElseThrow(() ->
                    new ApiException("User not found for id: " + userId, NOT_FOUND));

            if (user != null) {
                userRedisTemplate.opsForValue().set(userId.toString(), user);
                Objects.requireNonNull(cacheManager.getCache(ID_BY_USERNAME)).put(user.getUsername(), user.getId());
            }
        }
        return user;
    }

    public void deleteUser(UserEntity userEntity) {
        userRepository.deleteById(userEntity.getId());
        userRedisTemplate.delete(userEntity.getId().toString());
        Objects.requireNonNull(cacheManager.getCache(ID_BY_USERNAME)).evict(userEntity.getUsername());
    }

    public List<UserEntity> findAll() {
        List<UserEntity> allUsers = userRedisTemplate.opsForList().range(USERS_KEY, 0, -1);
        if (allUsers == null || allUsers.isEmpty()) {
            allUsers = userRepository.findAll();
            for (UserEntity user : allUsers) {
                userRedisTemplate.opsForValue().set(user.getId().toString(), user);
                Objects.requireNonNull(cacheManager.getCache(ID_BY_USERNAME)).put(user.getUsername(), user.getId());
            }
        }
        return allUsers;
    }

    public Optional<UserEntity> findByUsername(String username) {
        Cache cache = Objects.requireNonNull(cacheManager.getCache(ID_BY_USERNAME));
        Cache.ValueWrapper valueWrapper = cache.get(username);
        if (valueWrapper != null) {
            Long userId = (Long) valueWrapper.get();
            if (userId != null) {
                UserEntity user = userRedisTemplate.opsForValue().get(userId.toString());
                return Optional.ofNullable(user);
            }
        }
        return Optional.empty();
    }

    public void addAllToCache() {
        List<UserEntity> users = userRepository.findAll();
        for (UserEntity user : users) {
            userRedisTemplate.opsForValue().set(user.getId().toString(), user);
            Objects.requireNonNull(cacheManager.getCache(ID_BY_USERNAME)).put(user.getUsername(), user.getId());
        }
    }
}
