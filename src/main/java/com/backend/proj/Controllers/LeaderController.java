package com.backend.proj.Controllers;

import com.backend.proj.dtos.RegisterLeaderDto;

import com.backend.proj.dtos.UpdateLeaderDto;
import com.backend.proj.response.ApiResponse;
import com.backend.proj.serviceImpl.LeaderServiceImpl;
import com.backend.proj.utils.ResponseHandler;

import java.util.UUID;

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

    // registering leaders
    @PostMapping("/addLeader")
    public ResponseEntity<ApiResponse<Object>> PostSuggestion(@Valid @RequestBody RegisterLeaderDto dto)
            throws Exception {

        Object ob = leaderServiceImpl.registerNewLeader(dto).getData();
        return ResponseHandler.success(ob, HttpStatus.CREATED);
    }

    // getting all leaders on the system
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Object>> getLeaders() throws Exception {

        Object ob = leaderServiceImpl.getLeaders().getData();
        return ResponseHandler.success(ob, HttpStatus.OK);

    }

    // updating leaders
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Object>> updateLeader(@PathVariable("id") UUID id,
            @RequestBody UpdateLeaderDto dto) throws Exception {

        return ResponseHandler.success(leaderServiceImpl.updateLeader(dto, id).getData(), HttpStatus.CREATED);

    }

    // delete leader
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteLeader(@PathVariable("id") UUID id) throws Exception {

        return ResponseHandler.success(leaderServiceImpl.deleteLeader(id).getData(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getLeaderById(@PathVariable("id") UUID id)throws Exception{
        return ResponseHandler.success(leaderServiceImpl.getLeaderById(id), HttpStatus.OK);
    }

}
