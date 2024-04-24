package com.backend.proj.dtos;

import javax.validation.constraints.NotBlank;

import com.backend.proj.enums.ECategory;
import com.backend.proj.enums.EUrwego;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CreateEventsDto {
    private String eventName;
    private EUrwego organizationLevel;
    private String location;
    private ECategory category;
    private String startDateTime;
    private String endDateTime;
    // @NotBlank
    // @NonNull
    // private String startTime;
    // @NotBlank
    // @NonNull
    // private String endTime;
    private String descriptions;
}
