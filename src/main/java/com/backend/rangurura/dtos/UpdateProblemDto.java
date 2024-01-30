package com.backend.proj.dtos;

import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.backend.proj.enums.ECategory;
import com.backend.proj.enums.EUrwego;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProblemDto {
    private Optional<ECategory> category;
    private Optional<String> ikibazo;
    private Optional<EUrwego> urwego;
    private Optional<String> number;
    private MultipartFile proof;
    private MultipartFile record;
}
