package com.backend.proj.Services;

import java.util.UUID;

import com.backend.proj.dtos.CreateProblemDto;
import com.backend.proj.dtos.UpdateProblemDto;
import com.backend.proj.enums.EProblem_Status;
import com.backend.proj.response.ApiResponse;

public interface ProblemService {
    public ApiResponse<Object> createAProblem(CreateProblemDto dto) throws Exception;

    public Object getMyAskedProblems() throws Exception;

    public ApiResponse<Object> deleteQuestion(UUID id) throws Exception;

    public ApiResponse<Object> updateMyProblem(UpdateProblemDto dto, UUID id) throws Exception;

    public ApiResponse<Object> getMyLocalProblems() throws Exception;

    public ApiResponse<Object> getProblemById(UUID id) throws Exception;

    public ApiResponse<Object> getProblemsByStatus(EProblem_Status status)throws Exception;
    public ApiResponse<Object> getNumberOfAllProb()throws Exception;
    public ApiResponse<Object>getNumberOfPendingProblems() throws Exception;
    public ApiResponse<Object>getNumberOfApprovedProblems() throws Exception;
    public ApiResponse<Object>getNumberOfRejectedProblems() throws Exception;

}
