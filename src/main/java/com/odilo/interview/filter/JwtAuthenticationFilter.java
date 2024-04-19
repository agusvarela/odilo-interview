package com.odilo.interview.filter;

import com.odilo.interview.auth.AuthJWT;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.EMPTY;

@Slf4j
@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final int BEGIN_INDEX = 7;
    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";
    private static final String UNABLE_TO_GET_JWT_TOKEN_OR_JWT_TOKEN_HAS_EXPIRED_LOG_MESSAGE =
            "Unable to get JWT Token or JWT Token has expired";

    private final AuthJWT authJwt;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        getJwtFromRequest(request).ifPresent(token -> {
            try {
                if (authJwt.validateToken(token)) {
                    setSecurityContext(new WebAuthenticationDetailsSource().buildDetails(request), token);
                }
            } catch (IllegalArgumentException | MalformedJwtException | ExpiredJwtException e) {
                logger.error(UNABLE_TO_GET_JWT_TOKEN_OR_JWT_TOKEN_HAS_EXPIRED_LOG_MESSAGE);
            }
        });

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return UriMatcherUtil.getUriMatcher().matches(request);
    }

    private void setSecurityContext(WebAuthenticationDetails authDetails, String token) {
        final List<SimpleGrantedAuthority> grantedAuthorities = authJwt.getRolesFromToken(token).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        final UserDetails userDetails = new User(authJwt.getUsernameFromToken(token), EMPTY, grantedAuthorities);

        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                null, userDetails.getAuthorities());

        authentication.setDetails(authDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private static Optional<String> getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return Optional.of(bearerToken.substring(BEGIN_INDEX));
        }
        return Optional.empty();
    }
}
