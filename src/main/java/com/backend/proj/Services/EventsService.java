package com.backend.proj.Services;

import com.backend.proj.dtos.CreateEventsDto;
import com.backend.proj.dtos.UpdateEventDto;
import com.backend.proj.response.ApiResponse;

public interface EventsService {
    public ApiResponse<Object> createAEvent(CreateEventsDto dto) throws Exception;

    ApiResponse<Object> updateMyEvent(UpdateEventDto dto, Long id) throws Exception;
    ApiResponse<Object> deleteMyEvent(Long id) throws Exception;
    ApiResponse<Object> myRecentEvent() throws Exception;
    ApiResponse<Object> receivedEvent() throws Exception;




}
