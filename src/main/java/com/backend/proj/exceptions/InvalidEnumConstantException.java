package com.backend.proj.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidEnumConstantException extends RuntimeException {
    public InvalidEnumConstantException(String message) {
        super(message);
    }
}
