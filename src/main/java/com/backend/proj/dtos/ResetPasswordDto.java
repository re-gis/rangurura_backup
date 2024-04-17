package com.backend.proj.dtos;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResetPasswordDto {
    private String newPassword;
    private String otp;
    private String phone;
}
