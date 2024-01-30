package com.backend.proj.ExceptionHandlers;

import com.backend.proj.exceptions.BadRequestException;
import com.backend.proj.exceptions.MessageSendingException;
import com.backend.proj.exceptions.NotFoundException;
import com.backend.proj.exceptions.UnauthorisedException;
import com.backend.proj.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<String>> handleServiceException(BadRequestException e) {
        return new ResponseEntity<>(new ApiResponse<>(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleInternalServerErrors(Exception e) {
        return new ResponseEntity<>(new ApiResponse<>(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(new ApiResponse<>(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MessageSendingException.class)
    public ResponseEntity<ApiResponse<String>> handleMessageSendingException(MessageSendingException e) {
        return new ResponseEntity<>(new ApiResponse<>(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnauthorisedException.class)
    public ResponseEntity<ApiResponse<String>> handleMessageUnauthorisedException(UnauthorisedException e) {
        return new ResponseEntity<>(new ApiResponse<>(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }
}
