package com.backend.proj.Controllers;

import com.backend.proj.dtos.CancelEventDto;
import com.backend.proj.dtos.CreateEventsDto;
import com.backend.proj.dtos.UpdateEventDto;
import com.backend.proj.response.ApiResponse;
import com.backend.proj.serviceImpl.EventServiceImpl;
import com.backend.proj.utils.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/events")
public class EventsControllers {
    private final EventServiceImpl eventServiceImpl;

    @PostMapping("/send_event")
    public ResponseEntity<ApiResponse<Object>> createAEvent(@Valid @RequestBody CreateEventsDto dto) throws Exception {

        Object ob = eventServiceImpl.createAEvent(dto).getData();
        return ResponseHandler.success(ob, HttpStatus.CREATED);

    }

    @PutMapping("/update_event/{id}")
    public ResponseEntity<ApiResponse<Object>> updateMyEvent(@PathVariable("id") UUID id,
            @RequestBody UpdateEventDto dto) throws Exception {

        Object ob = eventServiceImpl.updateMyEvent(dto, id).getData();
        return ResponseHandler.success(ob, HttpStatus.CREATED);

    }

    @DeleteMapping("/delete_event/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteMyEvent(@PathVariable("id") UUID id) throws Exception {

        Object ob = eventServiceImpl.deleteMyEvent(id).getData();
        return ResponseHandler.success(ob, HttpStatus.OK);

    }

    @GetMapping("/my_events")
    public ResponseEntity<ApiResponse<Object>> myRecentEvent() throws Exception {

        Object ob = eventServiceImpl.myRecentEvent().getData();
        return ResponseHandler.success(ob, HttpStatus.OK);
    }

    @GetMapping("/receive_event")
    public ResponseEntity<ApiResponse<Object>> receivedEvent() throws Exception {
        Object ob = eventServiceImpl.receivedEvent().getData();
        return ResponseHandler.success(ob, HttpStatus.OK);
    }

    @GetMapping("/number_of_events")
    public ResponseEntity<ApiResponse<Object>> getNumberOfAllSuggestions() throws Exception {
        return ResponseHandler.success(eventServiceImpl.getNumberOfAllEvents(), HttpStatus.OK);
    }

    @PutMapping("/event/cancel")
    public ResponseEntity<ApiResponse<Object>> cancelEvent(@Valid @RequestBody CancelEventDto dto) throws Exception {
        return ResponseHandler.success(eventServiceImpl.cancelEvent(dto), HttpStatus.OK);
    }
}
