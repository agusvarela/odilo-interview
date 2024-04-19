package com.odilo.interview.filter;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class UriMatcherUtil {

    public static final RequestMatcher URI_MATCHER = new OrRequestMatcher(
            new AntPathRequestMatcher("/odilo/api/auth/**"),
            new AntPathRequestMatcher("**swagger**"),
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/api-docs**"),
            new AntPathRequestMatcher("/api-docs/**"),
            new AntPathRequestMatcher("/v2/api-docs"),
            new AntPathRequestMatcher("/v3/api-docs/**"),
            new AntPathRequestMatcher("/proxy/**"),
            new AntPathRequestMatcher("/favicon**"),
            new AntPathRequestMatcher("/configuration/security"),
            new AntPathRequestMatcher("/webjars/**")
    );

    public static RequestMatcher getUriMatcher() {
        return URI_MATCHER;
    }
}
