package com.backend.proj.dtos;

import org.springframework.web.multipart.MultipartFile;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolveProblemDto {
    private String message;
    // private MultipartFile file;
}
