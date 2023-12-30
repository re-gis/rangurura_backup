package com.backend.rangurura.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.rangurura.dtos.RegisterDto;
import com.backend.rangurura.dtos.VerifyOtpDto;
import com.backend.rangurura.response.ApiResponse;
import com.backend.rangurura.serviceImpl.UserServiceImpl;
import com.backend.rangurura.utils.ResponseHandler;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserServiceImpl userServiceImpl;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> registerUser(@Valid @RequestBody RegisterDto dto) throws Exception {
        try{

            Object ob = userServiceImpl.registerUser(dto);
            return ResponseHandler.success(ob, HttpStatus.CREATED);
        }catch(Exception e){
            return ResponseHandler.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/account/verify")
    public ResponseEntity<ApiResponse<Object>> verifyAccount(@Valid @RequestBody VerifyOtpDto dto) throws Exception {
        try{

            Object ob = userServiceImpl.verifyOtp(dto);
            return ResponseHandler.success(ob, HttpStatus.OK);
        }catch(Exception e){
            return ResponseHandler.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
