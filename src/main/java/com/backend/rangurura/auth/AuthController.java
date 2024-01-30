package com.backend.proj.auth;

import com.backend.proj.dtos.LoginDto;
import com.backend.proj.response.ApiResponse;
import com.backend.proj.utils.ResponseHandler;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> loginUser(@Valid @RequestBody LoginDto dto){
        try{
            Object ob = authService.loginUser(dto);
            return ResponseHandler.success(ob, HttpStatus.OK);
        }catch (Exception e){
            return ResponseHandler.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
