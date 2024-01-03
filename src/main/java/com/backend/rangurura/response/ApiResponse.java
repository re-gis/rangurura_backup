package com.backend.rangurura.response;

import com.backend.rangurura.entities.Suggestions;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String error;

    // Constructor for success response
    public ApiResponse(T data) {
        this.success = true;
        this.data = data;
        this.error = null;
    }

    // Constructor for error response
    public ApiResponse(String errorMessage) {
        this.success = false;
        this.error = errorMessage;
    }

    public ApiResponse(String suggestionSavedSuccessfully, Suggestions savedSuggestion) {
    }
}
