package com.vako.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@AllArgsConstructor
@Component
public class JellyExceptionHandler extends ResponseEntityExceptionHandler {
    private final JellyErrorMapper forgetfulnessErrorMapper;

    @ExceptionHandler(value = {JellyException.class})
    public ResponseEntity<Object> handleJellyException(JellyException ex, WebRequest request) {
        var error = forgetfulnessErrorMapper.jellyExceptionToJellyError(ex, request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<Object>(
                error.toJson(), headers, error.getStatus());
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        return handleJellyException(new JellyException(JellyExceptionType.INTERNAL), request);
    }
}