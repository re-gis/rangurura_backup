package com.backend.proj.response;

import com.backend.proj.enums.ECategory;
import com.backend.proj.enums.EUrwego;
import com.backend.proj.enums.URole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String name;
    private String province;
    private String district;
    private String sector;
    private String cell;
    private String village;
    private String nationalId;
    private String phoneNumber;
    private URole role;
    private ECategory category;
    private EUrwego urwego;
    private String office;
    private boolean isVerified;
    private String imageUrl;
    private UUID id;
}
