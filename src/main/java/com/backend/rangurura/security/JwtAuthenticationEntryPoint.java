package com.backend.rangurura.security;

import com.backend.rangurura.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.io.OutputStream;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ResponseEntity<Object> entity = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Sorry, you are not authorized to access this resource", authException.getMessage()));

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        OutputStream out = response.getOutputStream();
        new ObjectMapper().writeValue(out, entity.getBody());
        out.flush();  }

}
