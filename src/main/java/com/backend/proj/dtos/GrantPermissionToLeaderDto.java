package com.backend.proj.dtos;

import com.backend.proj.enums.ECategory;
import com.backend.proj.enums.EUrwego;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class GrantPermissionToLeaderDto {
    private String nationalId;
    private EUrwego organizationLevel;
    private String location;
    private ECategory category;
    private String role;

}
