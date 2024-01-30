package com.backend.proj.Controllers;

import com.backend.proj.dtos.RegisterLeaderDto;

import com.backend.proj.response.ApiResponse;
import com.backend.proj.serviceImpl.LeaderServiceImpl;
import com.backend.proj.utils.ResponseHandler;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/leaders")
public class LeaderController {

private final LeaderServiceImpl leaderServiceImpl;

    @PostMapping("/addLeader")
    public ResponseEntity<ApiResponse<Object>> PostSuggestion(@Valid @RequestBody RegisterLeaderDto dto) throws Exception {
        try {

            Object ob =leaderServiceImpl.registerNewLeader(dto);
            return ResponseHandler.success(ob, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseHandler.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
