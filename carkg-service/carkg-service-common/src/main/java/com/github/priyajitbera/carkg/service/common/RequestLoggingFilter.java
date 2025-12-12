package com.github.priyajitbera.carkg.service.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper =
                new ContentCachingRequestWrapper(request);
        log.info("{} {}", request.getMethod(), request.getRequestURL().toString());
        filterChain.doFilter(requestWrapper, response);
        log.info("{} {} {}", request.getMethod(), request.getRequestURL().toString(), requestWrapper.getContentAsString());
        log.info("{} {} {}", request.getMethod(), request.getRequestURL().toString(), response.getStatus());
    }
}
