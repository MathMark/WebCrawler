package com.experimental.webcrawler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ReportNotFoundException extends RuntimeException {
    
    public ReportNotFoundException(String message) {
        super(message);
    }
}
