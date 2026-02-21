package com.github.priyajitbera.carkg.service.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

  public static final String REQUEST_ID_KEY = "req-id";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    final String durationKey = DurationMeter.start();
    request.setAttribute(REQUEST_ID_KEY, newRequestId());
    ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
    log.info("{} {} {}", requestIdLog(), request.getMethod(), request.getRequestURL().toString());
    filterChain.doFilter(requestWrapper, response);
    log.info(
        "{} {} {} {}",
        requestIdLog(),
        request.getMethod(),
        request.getRequestURL().toString(),
        requestWrapper.getContentAsString());
    log.info(
        "{} {} {} {} duration: {}",
        requestIdLog(),
        request.getMethod(),
        request.getRequestURL().toString(),
        response.getStatus(),
        DurationMeter.durationLog(durationKey));
  }

  public static String requestIdLog() {
    return REQUEST_ID_KEY
        + ": "
        + RequestContextHolder.currentRequestAttributes()
            .getAttribute(REQUEST_ID_KEY, RequestAttributes.SCOPE_REQUEST);
  }

  public static String newRequestId() {
    return ZonedDateTime.now(ZoneId.of("UTC"))
            .format(DateTimeFormatter.ofPattern("yyyyMMddssHHmmss"))
        + "-"
        + UUID.randomUUID().toString().substring(0, 6);
  }
}
