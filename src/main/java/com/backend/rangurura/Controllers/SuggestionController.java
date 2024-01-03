package com.backend.rangurura.Controllers;

import com.backend.rangurura.Services.SuggestionService;
import com.backend.rangurura.dtos.RegisterDto;
import com.backend.rangurura.dtos.SuggestionDto;
import com.backend.rangurura.response.ApiResponse;
import com.backend.rangurura.utils.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/suggestions")
public class SuggestionController {
    private final SuggestionService suggestionService;
    @PostMapping("/post")
    public ResponseEntity<ApiResponse<Object>> PostSuggestion(@Valid @RequestBody SuggestionDto dto) throws Exception {
        try {

            Object ob =suggestionService.PostSuggestion(dto);
            return ResponseHandler.success(ob, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseHandler.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
