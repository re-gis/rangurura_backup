package com.backend.rangurura.Services;

import org.springframework.web.multipart.MultipartFile;

import com.backend.rangurura.dtos.CreateProblemDto;
import com.backend.rangurura.response.ApiResponse;

public interface ProblemService {
    public ApiResponse<Object> createAProblem(CreateProblemDto dto) throws Exception;
}
