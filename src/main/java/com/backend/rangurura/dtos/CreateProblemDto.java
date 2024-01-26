package com.backend.rangurura.dtos;

import org.springframework.web.multipart.MultipartFile;

import com.backend.rangurura.enums.ECategory;
import com.backend.rangurura.enums.EUrwego;

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
    private MultipartFile proof;
    private MultipartFile record;
}
