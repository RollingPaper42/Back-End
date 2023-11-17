package com.strcat.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResponseException extends Exception {
    public final Integer statusCode;
    public final String message;
}