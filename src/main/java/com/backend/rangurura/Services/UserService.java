package com.backend.rangurura.services;

import com.backend.rangurura.dtos.RegisterDto;
import com.backend.rangurura.response.ApiResponse;

public interface UserService {
    public ApiResponse<Object> registerUser(RegisterDto dto) throws Exception;
}
