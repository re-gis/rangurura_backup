package com.backend.proj.Controllers;

import com.backend.proj.dtos.CreateEventsDto;
import com.backend.proj.dtos.UpdateEventDto;
import com.backend.proj.response.ApiResponse;
import com.backend.proj.serviceImpl.EventServiceImpl;
import com.backend.proj.utils.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/events")
public class EventsControllers {
    private final EventServiceImpl eventServiceImpl;


    @PostMapping("/send_event")
    public ResponseEntity<ApiResponse<Object>>createAEvent(@Valid  @RequestBody CreateEventsDto dto) throws Exception{
    try{
        Object ob=eventServiceImpl.createAEvent(dto);
        return  ResponseHandler.success(ob,HttpStatus.CREATED);

    }catch (Exception e){
        return ResponseHandler.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    }
    @PutMapping("/update_event/{id}")
    public ResponseEntity<ApiResponse<Object>>updateMyEvent(@PathVariable("id") Long id, @RequestBody UpdateEventDto dto) throws Exception{
        try{
            Object ob=eventServiceImpl.updateMyEvent(dto,id);
            return ResponseHandler.success(ob,HttpStatus.CREATED);

        }catch (Exception e){
            return ResponseHandler.error(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/delete_event/{id}")
    public ResponseEntity<ApiResponse<Object>>deleteMyEvent(@PathVariable("id") Long id) throws Exception{
        try{
            Object ob=eventServiceImpl.deleteMyEvent(id);
            return ResponseHandler.success(ob,HttpStatus.OK);

        }catch (Exception e){
            return ResponseHandler.error(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/my_events")
    public ResponseEntity<ApiResponse<Object>>myRecentEvent() throws Exception{
        try{
            Object ob=eventServiceImpl.myRecentEvent();
            return ResponseHandler.success(ob,HttpStatus.OK);
        }catch (Exception e){
            return ResponseHandler.error(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
    @GetMapping("/receive_event")
    public ResponseEntity<ApiResponse<Object>>receivedEvent() throws Exception{
        try{
            Object ob=eventServiceImpl.receivedEvent();
            return ResponseHandler.success(ob,HttpStatus.OK);
        }catch (Exception e){
            return ResponseHandler.error(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
