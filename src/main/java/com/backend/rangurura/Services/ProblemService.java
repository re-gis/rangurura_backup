package com.backend.rangurura.Services;

import com.backend.rangurura.response.ApiResponse;

public interface ProblemService {
    public ApiResponse<Object> createAProblem() throws Exception;
}
