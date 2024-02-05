package com.backend.proj.response;

import com.backend.proj.entities.Problem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProblemResponse {
    private Problem problem;
    private String message;
}
