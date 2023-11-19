package com.vako.exception;

import lombok.Getter;

@Getter
public class JellyException extends RuntimeException {
    private final JellyExceptionType type;

    public JellyException(JellyExceptionType type) {
        super(type.getCode());
        this.type = type;
    }
}