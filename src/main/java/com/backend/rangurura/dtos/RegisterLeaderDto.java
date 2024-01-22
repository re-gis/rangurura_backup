package com.backend.rangurura.dtos;

import com.backend.rangurura.enums.ECategory;
import com.backend.rangurura.enums.EUrwego;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class RegisterLeaderDto {
    private String nationalId;
    private EUrwego organizationLevel;
    private String location;
    private ECategory category;
    private String phoneNumber;
}
