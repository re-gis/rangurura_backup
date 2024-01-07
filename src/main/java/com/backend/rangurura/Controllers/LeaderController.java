package com.backend.rangurura.Controllers;

import com.backend.rangurura.dtos.RegisterLeaderDto;
import com.backend.rangurura.dtos.SuggestionDto;
import com.backend.rangurura.entities.Leaders;

import com.backend.rangurura.response.ApiResponse;
import com.backend.rangurura.serviceImpl.LeaderServiceImpl;
import com.backend.rangurura.utils.ResponseHandler;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
