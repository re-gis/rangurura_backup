package com.backend.rangurura.Services;
import com.backend.rangurura.dtos.RegisterLeaderDto;

import com.backend.rangurura.response.ApiResponse;

import org.springframework.stereotype.Service;


@Service

public interface LeaderService {
//<<<<<<< HEAD
    public ApiResponse<Object> registerNewLeader(RegisterLeaderDto dto) throws  Exception;

    public ApiResponse<Object> getLocalLeaders() throws Exception;

    public ApiResponse<Object> getLeaders() throws Exception;
//    private final LeaderRepository leaderRepository;
//
//    // this is to get all leaders
//    public List<Leaders> getLeaders() {
//        System.out.println("All things are okay!");
//        return leaderRepository.findAll();
//    }
//
//    // this is to add new leader
//    public void addNewLeader(Leaders leaders) {
//        leaderRepository.save(leaders);
//
//    }

//    ApiResponse<Object> registerNewLeader(RegisterLeaderDto dto) throws  Exception;
//>>>>>>> 9f8584e (debuging)
}
