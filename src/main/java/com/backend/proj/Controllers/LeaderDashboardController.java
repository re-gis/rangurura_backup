package com.backend.proj.Controllers;


import com.backend.proj.response.ApiResponse;
import com.backend.proj.serviceImpl.ProblemServiceImpl;

import com.backend.proj.serviceImpl.SuggestionServiceImpl;
import com.backend.proj.utils.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/leader-dashboard")

public class LeaderDashboardController {
    private  final ProblemServiceImpl problemServiceImpl;
    private  final SuggestionServiceImpl suggestionServiceImpl;


    @GetMapping("/number_of_probs")
    public ResponseEntity<ApiResponse<Object>>getNumberOfProOnMyLevel() throws  Exception{
        return ResponseHandler.success(problemServiceImpl.getNumberOfProOnMyLevel().getData(),HttpStatus.OK);
    }

    @GetMapping("/number_of_pending_probs")
    public ResponseEntity<ApiResponse<Object>> getNumberOfPendingProbsOnMyLevel() throws  Exception{
        return ResponseHandler.success(problemServiceImpl.getNumberOfPendingProbsOnMyLevel().getData(),HttpStatus.OK);
    }

    @GetMapping("/number_of_approved_probs")
    public ResponseEntity<ApiResponse<Object>> getNumberOfApprovedProbsOnMyLevel() throws  Exception{
        return ResponseHandler.success(problemServiceImpl.getNumberOfApprovedProbsOnMyLevel().getData(),HttpStatus.OK);
    }

    @GetMapping("/number_of_suggestions")
    public ResponseEntity<ApiResponse<Object>>  getNumberOfAllOnMyLocal() throws  Exception{
        return ResponseHandler.success(suggestionServiceImpl.getNumberOfAllOnMyLocal().getData(),HttpStatus.OK);
    }




}
