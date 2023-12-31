package com.backend.rangurura.services;

import com.backend.rangurura.response.ApiResponse;

public interface ProblemService {
    public ApiResponse<Object> createAProblem() throws Exception;
}
