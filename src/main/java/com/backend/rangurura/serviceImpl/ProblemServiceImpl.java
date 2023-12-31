package com.backend.rangurura.serviceImpl;

import org.springframework.stereotype.Service;

import com.backend.rangurura.response.ApiResponse;
import com.backend.rangurura.services.ProblemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {
    @Override
    public ApiResponse<Object> createAProblem() throws Exception {
        try {
            // get logged in user
            return ApiResponse.builder()
                    .data("u")
                    .success(true)
                    .build();
        } catch (Exception e) {
            throw new Exception("Internal server error...");
        }
    }

}
