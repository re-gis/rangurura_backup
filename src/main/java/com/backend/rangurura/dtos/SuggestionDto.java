package com.backend.rangurura.dtos;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class SuggestionDto {
    private String  phoneNumber;
    private String category;
    private String urwego;
    private String igitekerezo;
    private String proof;
    private String record;
}

