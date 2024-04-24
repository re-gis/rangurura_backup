package com.backend.proj.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.backend.proj.enums.ECategory;
import com.backend.proj.enums.EUrwego;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

public class UpdateEventDto {
        private String eventName;
        private EUrwego organizationLevel;
        private String location;
        private ECategory category;
        private String startDateTime;
        private String endDateTime;
        private String descriptions;

}
