package com.odilo.interview.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint, Serializable {

    private static final String UNAUTHORIZED_REQUEST = """
            {
            "errorMessage": "Unauthorized resource access",
            "errorDescription": "Unauthorized request"
            }
            """;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getOutputStream().println(UNAUTHORIZED_REQUEST);
    }
}
