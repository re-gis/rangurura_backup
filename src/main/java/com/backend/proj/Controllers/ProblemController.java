package com.backend.proj.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.backend.proj.dtos.CreateProblemDto;
import com.backend.proj.dtos.UpdateProblemDto;
import com.backend.proj.entities.Problem;
import com.backend.proj.enums.EProblem_Status;
import com.backend.proj.response.ApiResponse;
import com.backend.proj.serviceImpl.ProblemServiceImpl;
import com.backend.proj.utils.Mapper;
import com.backend.proj.utils.ResponseHandler;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/problems")
public class ProblemController {
    private final ProblemServiceImpl problemServiceImpl;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Object>> createProblem(
            @RequestParam(value = "proof", required = false) MultipartFile proof,
            @RequestParam(value = "record", required = false) MultipartFile record,
            @RequestParam("details") String details) throws Exception {

        CreateProblemDto dto = Mapper.createProblemDto(details, proof, record);
        Object ob = problemServiceImpl.createAProblem(dto);
        return ResponseHandler.success(ob, HttpStatus.CREATED);

    }

    @GetMapping("/my/asked")
    public ResponseEntity<ApiResponse<Object>> getMyAskedProblems() throws Exception {

        Problem[] problems = problemServiceImpl.getMyAskedProblems();
        return ResponseHandler.success(problems, HttpStatus.OK);

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteMyProblem(@PathVariable("id") Long id) throws Exception {

        String response = problemServiceImpl.deleteQuestion(id);
        return ResponseHandler.success(response, HttpStatus.OK);

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Object>> updateMyAskedProblem(@PathVariable("id") Long id, UpdateProblemDto dto)
            throws Exception {

        return ResponseHandler.success(problemServiceImpl.updateMyProblem(dto, id), HttpStatus.CREATED);
    }

    @GetMapping("/local")
    public ResponseEntity<ApiResponse<Object>> getMyLocalProblems() throws Exception {

        return ResponseHandler.success(problemServiceImpl.getMyLocalProblems(), HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getProblemById(@PathVariable("id") Long id) throws Exception {

        return ResponseHandler.success(problemServiceImpl.getProblemById(id), HttpStatus.OK);

    }

    @GetMapping("/{status}")
    public ResponseEntity<ApiResponse<Object>> getProblemByStatus(@PathVariable("status") EProblem_Status status)
            throws Exception {

        return ResponseHandler.success(problemServiceImpl.getProblemsByStatus(status), HttpStatus.OK);

    }
}
