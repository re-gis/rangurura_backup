package com.backend.proj.utils;

import org.springframework.web.multipart.MultipartFile;

import com.backend.proj.dtos.CreateProblemDto;
import com.backend.proj.enums.ECategory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.backend.proj.exceptions.InvalidEnumConstantException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Mapper {
    public static ObjectMapper mapper = new ObjectMapper();
    private static final ValidateEnum validateEnum = new ValidateEnum();

    public static CreateProblemDto createProblemDto(String details, MultipartFile proof, MultipartFile record)
            throws Exception {
        try {
            JsonNode rootNode = mapper.readTree(details);
            String categoryStr = rootNode.get("category").asText();

            // Convert the category string to the ECategory enum type
            ECategory category = ECategory.valueOf(categoryStr.toUpperCase());

            validateEnum.isValidEnumConstant(category, ECategory.class);
            CreateProblemDto dto = mapper.readValue(details, CreateProblemDto.class);
            dto.setProof(proof);
            dto.setRecord(record);
            return dto;
        } catch (JsonMappingException | JsonParseException e) {
            throw new InvalidEnumConstantException("Invalid enum constant provided in the request.");
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }
}
