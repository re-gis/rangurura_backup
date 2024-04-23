package com.backend.proj.Controllers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.backend.proj.dtos.CreateProblemDto;
import com.backend.proj.dtos.EscalateProblemDto;
import com.backend.proj.dtos.UpdateProblemDto;
import com.backend.proj.enums.EProblem_Status;
import com.backend.proj.enums.EUrwego;
import com.backend.proj.repositories.ProblemRepository;
import com.backend.proj.response.ApiResponse;
import com.backend.proj.serviceImpl.ProblemServiceImpl;
import com.backend.proj.utils.Mapper;
import com.backend.proj.utils.ResponseHandler;
import com.backend.proj.entities.Problem;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/problems")
public class ProblemController {
    private final ProblemServiceImpl problemServiceImpl;
    private final ProblemRepository problemRepository;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Object>> createProblem(
            @RequestParam(value = "proof", required = false) MultipartFile proof,
            @RequestParam(value = "record", required = false) MultipartFile record,
            @RequestParam("details") String details) throws Exception {

        CreateProblemDto dto = Mapper.createProblemDto(details, proof, record);
        Object ob = problemServiceImpl.createAProblem(dto).getData();
        return ResponseHandler.success(ob, HttpStatus.CREATED);

    }

    @GetMapping("/my/asked")
    public ResponseEntity<ApiResponse<Object>> getMyAskedProblems() throws Exception {

        Object problems = problemServiceImpl.getMyAskedProblems().getData();
        return ResponseHandler.success(problems, HttpStatus.OK);

    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Object>> getAllProblems() throws Exception {
        Object problems = problemServiceImpl.getAllProblems().getData();
        return ResponseHandler.success(problems, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteMyProblem(@PathVariable("id") UUID id) throws Exception {

        Object response = problemServiceImpl.deleteQuestion(id).getData();
        return ResponseHandler.success(response, HttpStatus.OK);

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Object>> updateMyAskedProblem(@PathVariable("id") UUID id, UpdateProblemDto dto)
            throws Exception {

        return ResponseHandler.success(problemServiceImpl.updateMyProblem(dto, id).getData(), HttpStatus.CREATED);
    }

    @GetMapping("/local")
    public ResponseEntity<ApiResponse<Object>> getMyLocalProblems() throws Exception {

        return ResponseHandler.success(problemServiceImpl.getMyLocalProblems().getData(), HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getProblemById(@PathVariable("id") UUID id) throws Exception {

        return ResponseHandler.success(problemServiceImpl.getProblemById(id).getData(), HttpStatus.OK);

    }

    @GetMapping("/{status}")
    public ResponseEntity<ApiResponse<Object>> getProblemByStatus(@PathVariable("status") EProblem_Status status)
            throws Exception {

        return ResponseHandler.success(problemServiceImpl.getProblemsByStatus(status).getData(), HttpStatus.OK);

    }

    @GetMapping("/number_of_all_prob")
    public ResponseEntity<ApiResponse<Object>> getNumberOfAllProb() throws Exception {
        return ResponseHandler.success(problemServiceImpl.getNumberOfAllProb().getData(), HttpStatus.OK);
    }

    @GetMapping("/number_of_pending_problems")
    public ResponseEntity<ApiResponse<Object>> getNumberOfPendingProblems() throws Exception {
        return ResponseHandler.success(problemServiceImpl.getNumberOfPendingProblems().getData(), HttpStatus.OK);
    }

    @GetMapping("/number_of_approved_probs")
    public ResponseEntity<ApiResponse<Object>> getNumberOfApprovedProblems() throws Exception {
        return ResponseHandler.success(problemServiceImpl.getNumberOfApprovedProblems().getData(), HttpStatus.OK);
    }

    @GetMapping("/number_of_rejected_probs")
    public ResponseEntity<ApiResponse<Object>> getNumberOfRejectedProblems() throws Exception {
        return ResponseHandler.success(problemServiceImpl.getNumberOfApprovedProblems().getData(), HttpStatus.OK);
    }

    @PostMapping("escalate")
    public ResponseEntity<ApiResponse<Object>> escalateManually(@RequestBody EscalateProblemDto dto) throws Exception {
        return ResponseHandler.success(problemServiceImpl.escalateManually(dto).getData(), HttpStatus.OK);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public ResponseEntity<ApiResponse<Object>> escalateProblems() {
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minus(2, ChronoUnit.WEEKS);

        List<Problem> problems = problemRepository.findAllByStatusAndCreatedAtBefore(EProblem_Status.PENDING,
                twoWeeksAgo);

        if (problems.isEmpty()) {
            return ResponseHandler.success(ApiResponse.builder()
                    .data("No pending problems to be escalated at the moment...")
                    .success(true)
                    .status(HttpStatus.OK)
                    .build(), HttpStatus.OK);
        } else {
            for (Problem problem : problems) {
                switch (problem.getUrwego()) {
                    case UMUDUGUDU:
                        problem.setUrwego(EUrwego.AKAGARI);
                        break;
                    case AKAGARI:
                        problem.setUrwego(EUrwego.UMURENGE);
                        break;
                    case UMURENGE:
                        problem.setUrwego(EUrwego.AKARERE);
                        break;
                    case AKARERE:
                        problem.setUrwego(EUrwego.INTARA);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + problem.getUrwego());
                }
                problem.setStatus(EProblem_Status.ESCALATED);
                problemRepository.save(problem);
            }

            return ResponseHandler.success(ApiResponse.builder()
                    .data("Problems escalated successfully...")
                    .success(true)
                    .status(HttpStatus.OK)
                    .build(), HttpStatus.OK);
        }
    }
}
