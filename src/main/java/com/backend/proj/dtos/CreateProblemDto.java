package com.backend.proj.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.multipart.MultipartFile;

import com.backend.proj.enums.ECategory;
import com.backend.proj.enums.EUrwego;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateProblemDto {
    private ECategory category;
    private String ikibazo;
    private EUrwego urwego;
    private String phoneNumber;
    @JsonIgnore
    private MultipartFile proof;
    @JsonIgnore
    private MultipartFile record;
    private String nationalId;
    private String target;
}
