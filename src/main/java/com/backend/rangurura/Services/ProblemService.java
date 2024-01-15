package com.backend.rangurura.Services;


import com.backend.rangurura.dtos.CreateProblemDto;
import com.backend.rangurura.dtos.UpdateProblemDto;
import com.backend.rangurura.response.ApiResponse;

public interface ProblemService {
    public ApiResponse<Object> createAProblem(CreateProblemDto dto) throws Exception;
    public Object getMyAskedProblems() throws Exception;
    public String deleteQuestion(Long id) throws Exception;
    public ApiResponse<Object> updateMyProblem(UpdateProblemDto dto, Long id)throws Exception;
}
