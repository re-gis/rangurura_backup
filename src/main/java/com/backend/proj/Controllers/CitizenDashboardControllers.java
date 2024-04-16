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
@RequestMapping(path = "/api/v1/user-dashboard")

public class CitizenDashboardControllers {
    private  final ProblemServiceImpl problemServiceImpl;
    private  final SuggestionServiceImpl suggestionServiceImpl;
    @GetMapping("/number_of_probs_solvedforMe")
    public ResponseEntity<ApiResponse<Object>>getNumberOfSolvedProblemsByUser() throws  Exception{
        return ResponseHandler.success(problemServiceImpl.getNumberOfSolvedProblemsForUser().getData(),HttpStatus.OK);
    }

    @GetMapping("/number_of_pending_probsForMe")
    public  ResponseEntity<ApiResponse<Object>>getNumberOfPendingProblemsForUser() throws  Exception{
        return  ResponseHandler.success(problemServiceImpl.getNumberOfPendingProblemsForUser().getData(),HttpStatus.OK);
    }
    @GetMapping("/number_of_accepted_suggestionsForMe")
    public  ResponseEntity<ApiResponse<Object>>getNumberOfAcceptedSuggestionsForMe() throws  Exception{
        return  ResponseHandler.success(suggestionServiceImpl.getNumberOfAcceptedSuggestionForMe().getData(),HttpStatus.OK);
    }
}

