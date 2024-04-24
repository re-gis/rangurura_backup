package com.backend.proj.Controllers;

import com.backend.proj.dtos.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.proj.response.ApiResponse;
import com.backend.proj.serviceImpl.UserServiceImpl;
import com.backend.proj.utils.ResponseHandler;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserServiceImpl userServiceImpl;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> registerUser(@Valid @RequestBody RegisterDto dto) throws Exception {

        Object ob = userServiceImpl.registerUser(dto).getData();
        return ResponseHandler.success(ob, HttpStatus.CREATED);

    }

    @PostMapping("/account/verify")
    public ResponseEntity<ApiResponse<Object>> verifyAccount(@Valid @RequestBody VerifyOtpDto dto) throws Exception {

        Object ob = userServiceImpl.verifyOtp(dto).getData();
        return ResponseHandler.success(ob, HttpStatus.OK);

    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Object>> getMyProfile() throws Exception {
        return ResponseHandler.success(userServiceImpl.getLoggedInUser().getData(), HttpStatus.OK);

    }

    @PostMapping("/updateprofile")
    public ResponseEntity<ApiResponse<Object>> updateUser(@Valid @RequestBody UserUpdateDto dto) throws Exception {

        Object ob = userServiceImpl.updateUser(dto).getData();
        return ResponseHandler.success(ob, HttpStatus.OK);

    }

    @GetMapping("/admins")
    public ResponseEntity<ApiResponse<Object>> getAdmin() throws Exception {

        Object ob = userServiceImpl.getAdmins().getData();
        return ResponseHandler.success(ob, HttpStatus.OK);

    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Object>> getAllUsers() throws Exception {
        return ResponseHandler.success(userServiceImpl.getAllUsers().getData(), HttpStatus.OK);
    }

    @PostMapping("/otp/send")
    public ResponseEntity<ApiResponse<Object>> sendOtp(@RequestBody SendOtpDto dto) throws Exception {
        Object ob = userServiceImpl.sendOtp(dto).getData();
        return ResponseHandler.success(ob, HttpStatus.OK);
    }

    @PostMapping("/otp/resend")
    public ResponseEntity<ApiResponse<Object>> resendOtp(@RequestBody ResendOtpDto dto) throws Exception {
        Object ob = userServiceImpl.resendOtp(dto.getPhoneNumber()).getData();
        return ResponseHandler.success(ob, HttpStatus.OK);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@RequestBody ResetPasswordDto dto) throws Exception {
        Object ob = userServiceImpl.resetPassword(dto).getData();
        return ResponseHandler.success(ob, HttpStatus.OK);
    }
    @PostMapping("/get_user_by_national_id")
    public ResponseEntity<ApiResponse<Object>> getUserByNationalId(@RequestBody GetUserByNationalIdDto dto) throws Exception {
        Object ob = userServiceImpl.getUserByNationalId(dto).getData();
        return ResponseHandler.success(ob, HttpStatus.OK);
    }
}
