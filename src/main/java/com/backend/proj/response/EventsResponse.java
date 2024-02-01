package com.backend.proj.response;

import com.backend.proj.entities.Events;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventsResponse {

        private Events events;
        private String message;

}
