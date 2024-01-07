//package com.backend.rangurura.security;
//
//import com.backend.rangurura.response.ErrorResponse;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.stereotype.Component;
//import org.springframework.security.core.AuthenticationException;
//
//import java.io.IOException;
//import java.io.OutputStream;
//
//@Component
//@RequiredArgsConstructor
//public abstract class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
////    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//        ResponseEntity<Object> entity = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                .body(new ErrorResponse("Sorry, you are not authorized to access this resource", authException.getMessage()));
//
//        response.setStatus(HttpStatus.UNAUTHORIZED.value());
//        response.setContentType("application/json");
//        OutputStream out = response.getOutputStream();
//        new ObjectMapper().writeValue(out, entity.getBody());
//        out.flush();  }
//
//}
package com.backend.rangurura.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -7858869558953243875L;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
