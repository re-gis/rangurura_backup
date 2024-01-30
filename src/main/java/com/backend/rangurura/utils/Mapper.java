package com.backend.proj.utils;

import org.springframework.web.multipart.MultipartFile;

import com.backend.proj.dtos.CreateProblemDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.io.IOException;

public class Mapper {
    public static ObjectMapper mapper = new ObjectMapper();

    public static CreateProblemDto createProblemDto(String details, MultipartFile proof, MultipartFile record)
            throws Exception {
        try {
            CreateProblemDto dto = mapper.readValue(details, CreateProblemDto.class);
            dto.setProof(proof);
            dto.setRecord(record);
            return dto;
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }
}
