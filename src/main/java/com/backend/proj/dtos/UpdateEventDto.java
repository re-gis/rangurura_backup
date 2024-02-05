package com.backend.proj.dtos;

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
        private String startDate;
        private String endDate;
        private String startTime;
        private String endTime;
        private String descriptions;

}
