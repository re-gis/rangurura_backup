package com.backend.proj.auth;

import com.backend.proj.dtos.LoginDto;
import com.backend.proj.entities.User;
import com.backend.proj.exceptions.BadRequestException;
import com.backend.proj.exceptions.NotFoundException;
import com.backend.proj.exceptions.UnauthorisedException;
import com.backend.proj.repositories.UserRepository;
import com.backend.proj.response.ApiResponse;
import com.backend.proj.Services.JwtService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ApiResponse<Object> loginUser(LoginDto dto) throws Exception {
        if (dto.getNationalId() == null || dto.getPassword() == null) {
            throw new BadRequestException("All credentials are required!");
        }

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getNationalId(), dto.getPassword()));

        if (!auth.isAuthenticated()) {
            throw new BadRequestException("Authentication failed");
        }

        User user = userRepository.findByNationalId(dto.getNationalId())
                .orElseThrow(() -> new NotFoundException("User not found!"));

        if (!user.isVerified()) {
            throw new UnauthorisedException("Verify the account to continue!");
        }

        var token = jwtService.generateToken(user);        
        return ApiResponse.builder()
                .success(true)
                .data(token)
                .status(HttpStatus.OK)
                .build();

    }
}
