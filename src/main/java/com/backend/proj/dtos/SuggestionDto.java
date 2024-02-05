package com.backend.proj.dtos;

import com.backend.proj.enums.ECategory;
import com.backend.proj.enums.EUrwego;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SuggestionDto {
    private String phoneNumber;
    private ECategory category;
    private EUrwego urwego;
    private String igitekerezo;
    private String nationalId;
    private String upperLevel;
    private String location;
}
