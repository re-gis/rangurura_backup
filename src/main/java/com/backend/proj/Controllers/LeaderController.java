package com.backend.proj.Controllers;

import com.backend.proj.dtos.RegisterLeaderDto;

import com.backend.proj.dtos.UpdateLeaderDto;
import com.backend.proj.dtos.UpdateProblemDto;
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

//registering leaders
    @PostMapping("/addLeader")
    public ResponseEntity<ApiResponse<Object>> PostSuggestion(@Valid @RequestBody RegisterLeaderDto dto) throws Exception {
        try {

            Object ob =leaderServiceImpl.registerNewLeader(dto);
            return ResponseHandler.success(ob, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseHandler.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //getting all leaders on the system
    @GetMapping("/leaders")
    public ResponseEntity<ApiResponse<Object>> getLeaders() throws Exception{
        try {
            Object ob=leaderServiceImpl.getLeaders();
            return ResponseHandler.success(ob,HttpStatus.OK);

        }catch (Exception e){
            return  ResponseHandler.error(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //updating leaders
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Object>> updateLeader(@PathVariable("id") Long id,@RequestBody UpdateLeaderDto dto) {
        try {
            return ResponseHandler.success(leaderServiceImpl.updateLeader(dto, id), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseHandler.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //delete leader
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteLeader(@PathVariable("id") Long id) {
        try {
            return ResponseHandler.success(leaderServiceImpl.deleteLeader( id), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseHandler.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
