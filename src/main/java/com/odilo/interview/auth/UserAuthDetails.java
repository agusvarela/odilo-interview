package com.odilo.interview.auth;

import com.odilo.interview.exception.ApiException;
import com.odilo.interview.model.UserEntity;
import com.odilo.interview.repository.adapter.CacheUserRepositoryAdapter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Component
@AllArgsConstructor
public class UserAuthDetails implements UserDetailsService {

    private final CacheUserRepositoryAdapter cacheUserRepositoryAdapter;

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("Finding user: {}", username);

        UserEntity userEntity = cacheUserRepositoryAdapter.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found.", NOT_FOUND));

        log.info("User: {}, has been found", username);

        return new User(userEntity.getUsername(), userEntity.getPassword(),
                AuthorityUtils.createAuthorityList(userEntity.getRoles().toString()));
    }
}
