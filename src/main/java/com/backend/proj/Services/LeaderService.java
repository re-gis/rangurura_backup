package com.backend.proj.Services;
import com.backend.proj.dtos.RegisterLeaderDto;

import com.backend.proj.dtos.UpdateLeaderDto;
import com.backend.proj.response.ApiResponse;

import org.springframework.stereotype.Service;


@Service

public interface LeaderService {

    public ApiResponse<Object> registerNewLeader(RegisterLeaderDto dto) throws  Exception;

//    public ApiResponse<Object> getLocalLeaders() throws Exception;

    public ApiResponse<Object> getLeaders() throws Exception;
    public ApiResponse<Object> deleteLeader(Long id) throws Exception;
    public ApiResponse<Object>updateLeader(UpdateLeaderDto dto ,Long id) throws Exception;
}
