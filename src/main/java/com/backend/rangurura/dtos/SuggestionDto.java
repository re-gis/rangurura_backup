package com.backend.rangurura.dtos;

import com.backend.rangurura.enums.ECategory;
import com.backend.rangurura.enums.EUrwego;

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
