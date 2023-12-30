package com.backend.rangurura.services;

import com.backend.rangurura.dtos.RegisterDto;
import com.backend.rangurura.dtos.VerifyOtpDto;
import com.backend.rangurura.response.ApiResponse;

public interface UserService {
    public ApiResponse<Object> registerUser(RegisterDto dto) throws Exception;
    public ApiResponse<Object> verifyOtp(VerifyOtpDto dto) throws Exception;
}
