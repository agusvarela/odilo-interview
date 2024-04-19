package com.odilo.interview.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;

@Slf4j
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        logRequest(wrappedRequest);
        filterChain.doFilter(wrappedRequest, wrappedResponse);
        logResponse(wrappedRequest, wrappedResponse);

        wrappedResponse.copyBodyToResponse();
    }

    private void logRequest(ContentCachingRequestWrapper request) throws IOException {
        log.info("Incoming request - Method: {}, URL: {}", request.getMethod(), request.getRequestURI());
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            log.info("Request Header - {}: {}", headerName, headerValue);
        }
        if (request.getContentType() != null && request.getContentType().startsWith("application/json")) {
            log.info("Request Body: {}", getContent(request));
        }
    }

    private void logResponse(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) throws IOException {
        log.info("Outgoing response - Status: {}", response.getStatus());
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            String headerValue = response.getHeader(headerName);
            log.info("Response Header - {}: {}", headerName, headerValue);
        }
        if (response.getContentType() != null && response.getContentType().startsWith("application/json")) {
            log.info("Response Body: {}", getContent(response));
        }
    }

    private String getContent(ContentCachingRequestWrapper request) {
        return new String(request.getContentAsByteArray());
    }

    private String getContent(ContentCachingResponseWrapper response) {
        return new String(response.getContentAsByteArray());
    }
}
