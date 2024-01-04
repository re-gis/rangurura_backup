package com.backend.rangurura.dtos;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class RegisterLeaderDto {
    private String nationalId;
    private String organizationLevel;
    private String location;
    private String category;

}
