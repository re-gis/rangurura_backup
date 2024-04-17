package com.backend.proj.Services;

import java.util.UUID;

import com.backend.proj.dtos.SuggestionDto;
import com.backend.proj.dtos.SuggestionUpdateDto;
import com.backend.proj.enums.ESuggestion;
import com.backend.proj.response.ApiResponse;

public interface SuggestionService {
    ApiResponse<Object> PostSuggestion(SuggestionDto dto) throws Exception;

    ApiResponse<Object> UpdateSuggestion(SuggestionUpdateDto dto, UUID id) throws Exception;

    ApiResponse<Object> getAllMySuggestions() throws Exception;

    ApiResponse<Object> getSuggestionsByStatus(ESuggestion status) throws Exception;

    ApiResponse<Object> getMyLocalSuggestions() throws Exception;

    ApiResponse<Object> deleteMySuggestion(UUID id) throws Exception;

    ApiResponse<Object> getSuggestionById(UUID id) throws Exception;

    ApiResponse<Object> getAllSuggestions()throws Exception;
    ApiResponse<Object>getNumberOfAllSuggestions() throws Exception; //this is for admin
    ApiResponse<Object>getNumberOfAcceptedSuggestionForMe() throws  Exception ; //this is for citizen
//    ApiResponse<Object>getNumberOfAllOnMyLocal() throws  Exception ; //this is for citizen
}
