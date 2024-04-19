package com.odilo.interview.auth;

import com.odilo.interview.exception.ApiException;
import com.odilo.interview.model.Role;
import com.odilo.interview.model.UserEntity;
import com.odilo.interview.repository.adapter.CacheUserRepositoryAdapter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AuthJWT {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration.time}")
    private int ttl;

    private final CacheUserRepositoryAdapter cacheUserRepositoryAdapter;
    private final RedisTemplate<String, String> redisTemplate;

    public String generateJWTToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        UserEntity userEntity = cacheUserRepositoryAdapter.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found.", NOT_FOUND));

        List<String> roles = userEntity.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        long now = System.currentTimeMillis();
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

        String token = Jwts.builder()
                .claim("roles", roles)
                .subject(username)
                .issuedAt(new Date(now))
                .expiration(new Date(now + ttl))
                .signWith(key)
                .compact();

        redisTemplate.opsForValue().set(username, token, Duration.ofMillis(ttl));
        return token;
    }

    public boolean validateToken(String token) {
        String username = getUsernameFromToken(token);
        Optional<UserEntity> userOptional = cacheUserRepositoryAdapter.findByUsername(username);
        if (userOptional.isEmpty()) {
            return false;
        }

        UserEntity userEntity = userOptional.get();

        List<String> roles = userEntity.getRoles()
                .stream()
                .map(Role::getName)
                .toList();

        return Objects.nonNull(username) && !isTokenExpired(token) &&
                Boolean.TRUE.equals(redisTemplate.hasKey(username)) &&
                token.equals(redisTemplate.opsForValue().get(username)) &&
                roles.equals(getRolesFromToken(token));
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public List<String>  getRolesFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("roles", List.class));
    }

    public boolean isTokenExpired(String token) {
        return getClaimFromToken(token, Claims::getExpiration).before(new Date());
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return claimsResolver.apply(claims);
    }
}
