package com.vako.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum JellyExceptionType {
    INTERNAL("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_AUTHORIZED("Not logged in", HttpStatus.FORBIDDEN);

    private final String code;
    private final String description;
    private final HttpStatus status;

    JellyExceptionType(String description, HttpStatus status) {
        this.code = this.name();
        this.description = description;
        this.status = status;
    }
}