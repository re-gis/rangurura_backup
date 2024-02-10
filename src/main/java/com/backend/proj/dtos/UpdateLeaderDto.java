package com.backend.proj.dtos;

import com.backend.proj.enums.ECategory;
import com.backend.proj.enums.EUrwego;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Data
@NoArgsConstructor
@Builder

public class UpdateLeaderDto {
    private String nationalId;
    private EUrwego organizationLevel;
    private String location;
    private ECategory category;
}
