package com.strcat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionResolver {
    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public ResponseEntity<String> handleUnauthorizedException(AuthenticationException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증에 실패했습니다.");
    }

    @ExceptionHandler(NotAcceptableException.class)
    @ResponseBody
    public ResponseEntity<String> handleNotAcceptableException(NotAcceptableException exception) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(exception.getMessage());
    }
}
