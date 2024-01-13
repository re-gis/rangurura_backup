package com.backend.rangurura.serviceImpl;

import com.backend.rangurura.Services.SuggestionService;
import com.backend.rangurura.dtos.SuggestionDto;
import com.backend.rangurura.dtos.SuggestionUpdateDto;
import com.backend.rangurura.entities.Suggestions;
import com.backend.rangurura.exceptions.BadRequestException;
import com.backend.rangurura.repositories.SuggestionRepository;
import com.backend.rangurura.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@RequiredArgsConstructor
@Service
public class SuggestionServiceImpl implements SuggestionService {
    private final SuggestionRepository suggestionRepository;

    @Override
    public ApiResponse<Object> PostSuggestion(@Valid SuggestionDto dto) throws Exception {
        try {
            // Convert DTO to entity
            Suggestions suggestionEntity = convertDtoToEntity(dto);

            // Save the suggestion to the repository
            Suggestions savedSuggestion = suggestionRepository.save(suggestionEntity);
            if(savedSuggestion!=null) {
                return ApiResponse.builder()
                        .data("Suggestion sent successfully")
                        .success(true)
                        .build();
            }else{
                return  ApiResponse.builder()
                        .data("Failed to process suggestion")
                        .success(false)
                        .build();
            }
        } catch (Exception e) {
            throw new Exception("Failed to process suggestion", e);
        }
    }

    @Override
    public ApiResponse<Object> UpdateSuggestion(SuggestionUpdateDto dto) throws Exception {
        return null;
    }

    //this is to get the Id of the current suggestion


    private Suggestions convertDtoToEntity(SuggestionDto dto) {

        // Implement logic to convert DTO to Entity
        Suggestions suggestions = new Suggestions();

        suggestions.setUrwego(dto.getUrwego());
        suggestions.setPhoneNumber(dto.getPhoneNumber());
        suggestions.setIgitekerezo(dto.getIgitekerezo());
        suggestions.setCategory(dto.getCategory());
        suggestions.setProof(dto.getProof());
        suggestions.setRecord(dto.getRecord());

        return suggestions;
    }
}
