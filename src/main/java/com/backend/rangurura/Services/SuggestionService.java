package com.backend.rangurura.Services;

import com.backend.rangurura.dtos.SuggestionDto;
import com.backend.rangurura.dtos.SuggestionUpdateDto;
import com.backend.rangurura.response.ApiResponse;

public interface SuggestionService {
    ApiResponse<Object> PostSuggestion(SuggestionDto dto) throws Exception ;
    ApiResponse<Object> UpdateSuggestion(SuggestionUpdateDto dto) throws Exception ;


}
