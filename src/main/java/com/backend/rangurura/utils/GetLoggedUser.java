package com.backend.rangurura.utils;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.backend.rangurura.entities.User;
import com.backend.rangurura.exceptions.NotFoundException;
import com.backend.rangurura.exceptions.UnauthorisedException;
import com.backend.rangurura.repositories.UserRepository;
import com.backend.rangurura.response.UserResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GetLoggedUser {
    private final UserRepository userRepository;

    public UserResponse getLoggedUser() throws Exception {
        try {
            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == "anonymousUser") {
                throw new UnauthorisedException(("You are not logged in"));
            }

            String nationalId;
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (principal instanceof UserDetails) {
                nationalId = ((UserDetails) principal).getUsername();
            } else {
                nationalId = principal.toString();
            }

            Optional<User> user = userRepository.findByNationalId(nationalId);
            if (!user.isPresent()) {
                throw new NotFoundException("User not found!");
            }

            UserResponse u = UserResponse.builder()
                    .name(user.get().getUsername())
                    .nationalId(user.get().getNationalId())
                    .province(user.get().getProvince())
                    .district(user.get().getDistrict())
                    .sector(user.get().getSector())
                    .cell(user.get().getCell())
                    .village(user.get().getVillage())
                    .phoneNumber(user.get().getPhone())
                    .role(user.get().getRole())
                    .build();
            return u;
        } catch (NotFoundException e) {
            throw new NotFoundException("User not found!");
        } catch (Exception e) {
            throw new Exception("Internal server error...");
        }
    }
}