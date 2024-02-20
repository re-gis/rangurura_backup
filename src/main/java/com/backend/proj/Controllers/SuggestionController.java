package com.backend.proj.Controllers;

import com.backend.proj.dtos.SuggestionDto;
import com.backend.proj.dtos.SuggestionUpdateDto;
import com.backend.proj.enums.ESuggestion;
import com.backend.proj.response.ApiResponse;
import com.backend.proj.serviceImpl.SuggestionServiceImpl;
import com.backend.proj.utils.ResponseHandler;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

        Object ob = suggestionServiceImpl.PostSuggestion(dto);
        return ResponseHandler.success(ob, HttpStatus.CREATED);

    }

    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<Object>> getMySuggestions() throws Exception {

        return ResponseHandler.success(suggestionServiceImpl.getAllMySuggestions(), HttpStatus.OK);

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Object>> updateSuggestion(@RequestBody SuggestionUpdateDto dto,
            @PathVariable("id") Long id) throws Exception {

        return ResponseHandler.success(suggestionServiceImpl.UpdateSuggestion(dto, id), HttpStatus.OK);

    }

    @GetMapping("/{status}")
    public ResponseEntity<ApiResponse<Object>> getSuggestionsByStatus(@PathVariable("status") ESuggestion status)
            throws Exception {

        return ResponseHandler.success(suggestionServiceImpl.getSuggestionsByStatus(status), HttpStatus.OK);

    }

    @GetMapping("/local")
    public ResponseEntity<ApiResponse<Object>> getMyLocalSuggestions() throws Exception {

        return ResponseHandler.success(suggestionServiceImpl.getMyLocalSuggestions(), HttpStatus.OK);

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteMySuggestion(@PathVariable("id") Long id) throws Exception {

        return ResponseHandler.success(suggestionServiceImpl.deleteMySuggestion(id), HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getSuggestionById(@PathVariable("id") Long id) throws Exception {

        return ResponseHandler.success(suggestionServiceImpl.getSuggestionById(id), HttpStatus.OK);

    }

}
