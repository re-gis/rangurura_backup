package com.backend.proj.utils;

import java.util.Optional;

import com.backend.proj.exceptions.JwtExpiredException;
import com.backend.proj.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.backend.proj.entities.Leaders;
import com.backend.proj.entities.User;
import com.backend.proj.enums.URole;
import com.backend.proj.exceptions.NotFoundException;
import com.backend.proj.exceptions.UnauthorisedException;
import com.backend.proj.repositories.LeaderRepository;
import com.backend.proj.repositories.UserRepository;
import com.backend.proj.response.UserResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GetLoggedUser {
    private final UserRepository userRepository;
    private final LeaderRepository leaderRepository;

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
            } else {

                UserResponse u;

                if (user.get().getRole() == URole.UMUYOBOZI) {
                    // get the leader also and his details
                    Optional<Leaders> leader = leaderRepository.findByNationalId(nationalId);
                    if (leader == null) {
                        throw new NotFoundException(String.format("Leader %s not found!", nationalId));
                    }
                    u = UserResponse.builder()
                            .name(user.get().getRealName())
                            .nationalId(user.get().getNationalId())
                            .province(user.get().getProvince())
                            .district(user.get().getDistrict())
                            .sector(user.get().getSector())
                            .cell(user.get().getCell())
                            .village(user.get().getVillage())
                            .phoneNumber(user.get().getPhone())
                            .role(user.get().getRole())
                            .leaderRole(leader.get().getRole())
                            .category(leader.get().getCategory())
                            .urwego(leader.get().getOrganizationLevel())
                            .office(leader.get().getLocation())
                            .isVerified(user.get().isVerified())
                            .build();

                } else {

                    u = UserResponse.builder()
                            .name(user.get().getRealName())
                            .nationalId(user.get().getNationalId())
                            .province(user.get().getProvince())
                            .district(user.get().getDistrict())
                            .sector(user.get().getSector())
                            .cell(user.get().getCell())
                            .village(user.get().getVillage())
                            .phoneNumber(user.get().getPhone())
                            .role(user.get().getRole())
                            .isVerified(user.get().isVerified())
                            .build();
                }
                return u;
            }
        } catch (JwtExpiredException e) {
            throw new JwtExpiredException("Jwt expired: " + e.getMessage());
        } catch (NotFoundException e) {
            throw new NotFoundException("User not found!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
}