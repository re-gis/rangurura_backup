package com.backend.rangurura.ExceptionHandlers;

import com.backend.rangurura.exceptions.BadRequestException;
import com.backend.rangurura.exceptions.NotFoundException;
import com.backend.rangurura.response.ApiResponse;
import org.springframework.data.crossstore.ChangeSetPersister;
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
    public ResponseEntity<ApiResponse<String>> handleInternalServerErrors(Exception e){
        return new ResponseEntity<>(new ApiResponse<>(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(new ApiResponse<>(e.getMessage()), HttpStatus.NOT_FOUND);
    }
}
