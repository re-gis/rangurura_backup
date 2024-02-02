package com.backend.proj.Services;

import com.backend.proj.dtos.SuggestionDto;
import com.backend.proj.dtos.SuggestionUpdateDto;
import com.backend.proj.enums.ESuggestion;
import com.backend.proj.response.ApiResponse;

public interface SuggestionService {
    ApiResponse<Object> PostSuggestion(SuggestionDto dto) throws Exception;

    ApiResponse<Object> UpdateSuggestion(SuggestionUpdateDto dto, Long id) throws Exception;

    ApiResponse<Object> getAllMySuggestions() throws Exception;

    ApiResponse<Object> getSuggestionsByStatus(ESuggestion status) throws Exception;

    ApiResponse<Object> getMyLocalSuggestions() throws Exception;

    ApiResponse<Object> deleteMySuggestion(Long id) throws Exception;

    ApiResponse<Object> getSuggestionById(Long id) throws Exception;

}
