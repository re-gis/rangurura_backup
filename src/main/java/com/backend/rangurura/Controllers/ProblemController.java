package com.backend.rangurura.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.rangurura.dtos.CreateProblemDto;
import com.backend.rangurura.response.ApiResponse;
import com.backend.rangurura.serviceImpl.ProblemServiceImpl;
import com.backend.rangurura.utils.Mapper;
import com.backend.rangurura.utils.ResponseHandler;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/problems")
public class ProblemController {
    private final ProblemServiceImpl problemServiceImpl;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Object>> createProblem(@RequestParam("proof") MultipartFile proof,
            @RequestParam("record") MultipartFile record, @RequestParam("details") String details)
            throws Exception {
        try {
            CreateProblemDto dto = Mapper.createProblemDto(details, proof, record);
            Object ob = problemServiceImpl.createAProblem(dto);
            return ResponseHandler.success(ob, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseHandler.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
