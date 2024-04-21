package com.backend.proj.dtos;

import com.backend.proj.enums.ECategory;
import com.backend.proj.enums.EUrwego;
import com.backend.proj.enums.LRole;
import com.backend.proj.enums.URole;

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
    private String name;
    private String cell;
    private String village;
    private String district;
    private String sector;
    private String province;
    private LRole role;
}
