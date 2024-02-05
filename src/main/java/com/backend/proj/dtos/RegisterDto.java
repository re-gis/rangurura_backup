package com.backend.proj.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterDto {
    private String name;
    private String province;
    private String district;
    private String sector;
    private String cell;
    private String village;
    private String nationalId;
    private String password;
    private String cpassword;
    private String phoneNumber; 
}
