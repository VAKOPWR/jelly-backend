package com.vako.exception;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class JellyErrorMapper {
    JellyError jellyExceptionToJellyError(JellyException ex, WebRequest request) {
        ServletWebRequest webRequest = (ServletWebRequest) request;
        return new JellyError(
                ex.getType().getCode(),
                ex.getType().getDescription(),
                webRequest.getRequest().getRequestURI(),
                LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                ex.getType().getStatus()
        );
    }
}