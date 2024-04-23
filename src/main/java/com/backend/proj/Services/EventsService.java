package com.backend.proj.Services;

import java.util.UUID;

import com.backend.proj.dtos.CancelEventDto;
import com.backend.proj.dtos.CreateEventsDto;
import com.backend.proj.dtos.UpdateEventDto;
import com.backend.proj.response.ApiResponse;

public interface EventsService {
    public ApiResponse<Object> createAEvent(CreateEventsDto dto) throws Exception;

    ApiResponse<Object> updateMyEvent(UpdateEventDto dto, UUID id) throws Exception;

    ApiResponse<Object> deleteMyEvent(UUID id) throws Exception;

    ApiResponse<Object> myRecentEvent() throws Exception;

    ApiResponse<Object> receivedEvent() throws Exception;

    ApiResponse<Object> getNumberOfAllEvents() throws Exception;

    ApiResponse<Object> getNumberOfAllEventsByMe() throws Exception;

    ApiResponse<Object> cancelEvent(CancelEventDto dto)throws Exception;

}
