package com.backend.proj.Services;

import com.backend.proj.dtos.RegisterDto;
import com.backend.proj.dtos.ResetPasswordDto;
import com.backend.proj.dtos.SendOtpDto;
import com.backend.proj.dtos.UserUpdateDto;
import com.backend.proj.dtos.VerifyOtpDto;
import com.backend.proj.response.ApiResponse;

public interface UserService {
    public ApiResponse<Object> registerUser(RegisterDto dto) throws Exception;

    public ApiResponse<Object> verifyOtp(VerifyOtpDto dto) throws Exception;

    public ApiResponse<Object> getLoggedInUser() throws Exception;

    // this is to get admins
    ApiResponse<Object> getAdmins() throws Exception;

    // this is to update the user details
    ApiResponse<Object> updateUser(UserUpdateDto dto) throws Exception;

    ApiResponse<Object> getAllUsers() throws Exception;

    ApiResponse<Object> resetPassword(ResetPasswordDto dto) throws Exception;

    ApiResponse<Object> sendOtp(SendOtpDto dto) throws Exception;

    boolean verifyOtp(String otp, String phone) throws Exception;

}
