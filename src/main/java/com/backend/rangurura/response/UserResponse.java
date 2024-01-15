package com.backend.rangurura.response;

import com.backend.rangurura.enums.URole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
