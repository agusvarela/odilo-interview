package com.odilo.interview.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final String ACCESS_DENIED_MESSAGE = """
            {
            "errorMessage": "Access Denied",
            "errorDescription": "You do not have permission to access this resource"
            }
            """;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().println(ACCESS_DENIED_MESSAGE);
    }
}
