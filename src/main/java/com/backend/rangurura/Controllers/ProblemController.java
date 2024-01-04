package com.backend.rangurura.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.rangurura.dtos.CreateProblemDto;
import com.backend.rangurura.response.ApiResponse;
import com.backend.rangurura.serviceImpl.ProblemServiceImpl;
import com.backend.rangurura.utils.ResponseHandler;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/problems")
public class ProblemController {
    private final ProblemServiceImpl problemServiceImpl;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Object>> PostSuggestion(@Valid @RequestBody CreateProblemDto dto)
            throws Exception {
        try {
            Object ob = problemServiceImpl.createAProblem(dto);
            return ResponseHandler.success(ob, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseHandler.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
