package com.backend.proj.dtos;

import com.backend.proj.enums.ECategory;
import com.backend.proj.enums.ESuggestion;
import com.backend.proj.enums.EUrwego;

import lombok.Data;

@Data
public class SuggestionUpdateDto {
    private String phoneNumber;
    private String nationalId;
    private EUrwego urwego;
    private String location;
    private String upperLevel;
    private ECategory category;
    private String igitekerezo;
    private ESuggestion status;
}
