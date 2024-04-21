package com.backend.proj.dtos;

import java.util.UUID;

import com.backend.proj.enums.EUrwego;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EscalateProblemDto {
    private UUID problemId;
    private EUrwego nextUrwego;
    private String target;
    // private 
}
