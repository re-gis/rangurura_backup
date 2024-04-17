package com.backend.proj.ExceptionHandlers;

import com.backend.proj.exceptions.BadRequestException;
import com.backend.proj.exceptions.InvalidEnumConstantException;
import com.backend.proj.exceptions.JwtExpiredException;
import com.backend.proj.exceptions.MessageSendingException;
import com.backend.proj.exceptions.NotFoundException;
import com.backend.proj.exceptions.UnauthorisedException;
import com.backend.proj.response.ApiResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import io.jsonwebtoken.ExpiredJwtException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    @ExceptionHandler(InvalidEnumConstantException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidEnumConstantException(InvalidEnumConstantException e) {
        return new ResponseEntity<>(new ApiResponse<>(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JwtExpiredException.class)
    public ResponseEntity<ApiResponse<String>> handleJwtException(JwtExpiredException e) {
        return new ResponseEntity<>(new ApiResponse<>(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<String>> handleJwtExpiredException(ExpiredJwtException e) {
        return new ResponseEntity<>(new ApiResponse<>(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ApiResponse<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        if (e.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ex = (InvalidFormatException) e.getCause();
            if (ex.getTargetType().isEnum()) {
                return new ResponseEntity<>(new ApiResponse<>("Invalid value for enum: " + ex.getValue()), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(new ApiResponse<>("Invalid request payload"), HttpStatus.BAD_REQUEST);
    }
}
