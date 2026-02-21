package com.github.priyajitbera.carkg.service.api.controller;

import com.github.priyajitbera.carkg.service.api.model.response.ErrorModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@ControllerAdvice
public class CommonExceptionHandler {

  @ExceptionHandler
  public ResponseEntity<ErrorModel> handleResponseStatusException(ResponseStatusException ex) {
    log.error(
        "[handleResponseStatusException] Logging :{}, :{}",
        ex.getClass().getSimpleName(),
        ex.getMessage(),
        ex);
    return ResponseEntity.status(ex.getStatusCode())
        .body(
            ErrorModel.builder().code(ex.getStatusCode().value()).message(ex.getReason()).build());
  }

  @ExceptionHandler
  public ResponseEntity<ErrorModel> handleException(Exception ex) {
    log.error(
        "[handleException] Logging: {}, :{}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
    return ResponseEntity.badRequest()
        .body(
            ErrorModel.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .build());
  }
}
