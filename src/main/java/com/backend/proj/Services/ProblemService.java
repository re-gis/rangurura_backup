package com.backend.proj.Services;

import com.backend.proj.dtos.CreateProblemDto;
import com.backend.proj.dtos.UpdateProblemDto;
import com.backend.proj.enums.EProblem_Status;
import com.backend.proj.response.ApiResponse;

public interface ProblemService {
    public ApiResponse<Object> createAProblem(CreateProblemDto dto) throws Exception;

    public Object getMyAskedProblems() throws Exception;

    public ApiResponse<Object> deleteQuestion(Long id) throws Exception;

    public ApiResponse<Object> updateMyProblem(UpdateProblemDto dto, Long id) throws Exception;

    public ApiResponse<Object> getMyLocalProblems() throws Exception;

    public ApiResponse<Object> getProblemById(Long id) throws Exception;

    public ApiResponse<Object> getProblemsByStatus(EProblem_Status status)throws Exception;
}
