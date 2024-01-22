package com.backend.rangurura.Controllers;

import com.backend.rangurura.dtos.SuggestionDto;
import com.backend.rangurura.response.ApiResponse;
import com.backend.rangurura.serviceImpl.SuggestionServiceImpl;
import com.backend.rangurura.utils.ResponseHandler;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/suggestions")
public class SuggestionController {
    private final SuggestionServiceImpl suggestionServiceImpl;

    @PostMapping("/send_idea")
    public ResponseEntity<ApiResponse<Object>> PostSuggestion(@Valid @RequestBody SuggestionDto dto) throws Exception {
        try {

            Object ob = suggestionServiceImpl.PostSuggestion(dto);
            return ResponseHandler.success(ob, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseHandler.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<Object>> getMySuggestions(){
        try{
            return ResponseHandler.success(suggestionServiceImpl.getAllMySuggestions(), HttpStatus.OK);
        }catch(Exception e){
            return ResponseHandler.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
