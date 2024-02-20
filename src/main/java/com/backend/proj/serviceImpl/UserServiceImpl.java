package com.backend.proj.serviceImpl;

import java.util.List;
import java.util.Optional;

import com.backend.proj.dtos.UserUpdateDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.proj.dtos.RegisterDto;
import com.backend.proj.dtos.VerifyOtpDto;
import com.backend.proj.entities.Otp;
import com.backend.proj.entities.User;
import com.backend.proj.enums.URole;
import com.backend.proj.exceptions.BadRequestException;
import com.backend.proj.exceptions.MessageSendingException;
import com.backend.proj.exceptions.NotFoundException;
import com.backend.proj.repositories.OtpRepository;
import com.backend.proj.repositories.UserRepository;
import com.backend.proj.response.ApiResponse;
import com.backend.proj.response.UserResponse;
import com.backend.proj.Services.UserService;
import com.backend.proj.utils.GetLoggedUser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final OtpServiceImpl otpServiceImpl;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final GetLoggedUser getLoggedUser;

    @Override
    public ApiResponse<Object> registerUser(RegisterDto dto) throws Exception {
        try {
            if (dto.getName() == null || dto.getProvince() == null || dto.getDistrict() == null
                    || dto.getSector() == null || dto.getCell() == null || dto.getVillage() == null
                    || dto.getPassword() == null || dto.getCpassword() == null || dto.getNationalId() == null) {
                throw new BadRequestException("All credentials are required!");
            }

            if (!dto.getPassword().equals(dto.getCpassword())) {
                return ApiResponse.builder()
                        .data("Confirm password to continue...")
                        .success(false)
                        .build();
            }

            // check if the user doesn't exists
            Optional<User> eUser = userRepository.findOneByNationalId(dto.getNationalId());
            Optional<User> euser = userRepository.findOneByPhone(dto.getPhoneNumber());
            if (eUser.isPresent() || euser.isPresent()) {
                return ApiResponse.builder()
                        .data("Indangamuntu cyangwa numero yawe isanzwe muri proj yihindure wongere ugerageze cyangwa winjire...")
                        .success(false)
                        .build();
            }

            // send the message
            String o = otpServiceImpl.generateOtp(6);
            System.out.println(o);
            String message = "Your verification code to proj is: " + o;
            otpServiceImpl.sendMessage(dto.getPhoneNumber(), message);

            Otp otp = new Otp();
            otp.setNumber(dto.getPhoneNumber());
            otp.setOtp(passwordEncoder.encode(o));

            User user = new User();
            user.setNationalId(dto.getNationalId());
            user.setUsername(dto.getName());
            user.setPhone(dto.getPhoneNumber());
            user.setCell(dto.getCell());
            user.setVillage(dto.getVillage());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setProvince(dto.getProvince());
            user.setDistrict(dto.getDistrict());
            user.setSector(dto.getSector());
            user.setImageUrl("https://icon-library.com/images/no-user-image-icon/no-user-image-icon-0.jpg");
            user.setVerified(false);
            user.setRole(URole.ADMIN);
            // save the otp and user
            Optional<Otp> eOtp = otpRepository.findOneByNumber(dto.getPhoneNumber());
            if (eOtp.isPresent()) {
                return ApiResponse.builder()
                        .data("User already signed up, verify to continue...")
                        .success(false)
                        .build();
            }

            otpRepository.save(otp);
            userRepository.save(user);
            return ApiResponse.builder()
                    .success(true)
                    .data("Urakoze kwiyandikisha muri proj! Ubu ushobora kwinjiramo ugatanga ikibazo cyawe!")
                    .build();
        } catch (BadRequestException e) {
            throw new BadRequestException("All credentials are required!");
        } catch (MessageSendingException e) {
            throw new MessageSendingException("Error while sending the message...");
        } catch (Exception e) {
            throw new Exception("Internal server error...");
        }
    }

    @Override
    public ApiResponse<Object> verifyOtp(VerifyOtpDto dto) throws Exception {
        try {
            if (dto.getNumber() == null || dto.getOtp() == null) {
                throw new BadRequestException("All details are required...");
            }

            // check if the otp exists
            Optional<Otp> eotp = otpRepository.findOneByNumber(dto.getNumber());
            if (!eotp.isPresent()) {
                throw new BadRequestException("Invalid otp!");
            }

            Otp otp = eotp.get();
            if (!passwordEncoder.matches(dto.getOtp(), otp.getOtp())) {
                throw new BadRequestException("Invalid OTP!");
            }

            // delete the otp
            otpRepository.delete(otp);
            User euser = userRepository.findOneByPhone(eotp.get().getNumber())
                    .orElseThrow(() -> new NotFoundException("User not found!"));
            euser.setVerified(true);
            userRepository.save(euser);

            return ApiResponse.builder()
                    .success(true)
                    .data("Account verified successfully...")
                    .build();
        } catch (BadRequestException e) {
            throw new BadRequestException("Invalid OTP!");
        } catch (Exception e) {
            throw new Exception("Internal server error...");
        }
    }

    @Override
    public ApiResponse<Object> getLoggedInUser() throws Exception {
        try {
            UserResponse u = getLoggedUser.getLoggedUser();
            return ApiResponse.builder()
                    .data(u)
                    .success(true)
                    .build();
        } catch (NotFoundException e) {
            throw new NotFoundException("User not found!");
        } catch (Exception e) {
            throw new Exception("Internal server error...");
        }
    }

    // //this is the function to find the id of the logged user

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                // Principal is an instance of UserDetails
                UserDetails userDetails = (UserDetails) principal;
                return userRepository.findOneByNationalId(userDetails.getUsername())
                        .map(User::getId)
                        .orElse(null);
            } else if (principal instanceof String) {
                // Principal is a String, handle it accordingly
                // Example: return userRepository.findOneByUsername((String)
                // principal).map(User::getId).orElse(null);
            }
        }

        return null;
    }

    //this is to get admins
    @Override
    public ApiResponse<Object> getAdmins() throws Exception {
        try {

            // find all leaders
            List<User> leaders = userRepository.findByRole(URole.valueOf("ADMIN"));

            // Check if the list is empty
            if (leaders.isEmpty()) {
                throw new NotFoundException("No admins found in system!");
            }

            // You can further process the list of events as needed

            return ApiResponse.builder()
                    .data(leaders)
                    .success(true)
                    .build();
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Internal server error...");
        }
    }


    // this is to update the user details
    @Override
    public ApiResponse<Object> updateUser(UserUpdateDto dto) throws Exception {
        try {
            Long loggedInUserId = getCurrentUserId();
            System.out.println("The logged Id is " + loggedInUserId);

            if (loggedInUserId == null) {
                throw new NotFoundException("User not authenticated!");
            }

            Optional<User> existingUserOptional = userRepository.findById(loggedInUserId);
            if (existingUserOptional.isPresent()) {
                User existingUser = existingUserOptional.get();

                // Validate input DTO
                validateUpdateDto(dto);

                // Update the fields you want to modify
                existingUser.setUsername(dto.getName());
                existingUser.setCell(dto.getCell());
                existingUser.setVillage(dto.getVillage());
                existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
                existingUser.setProvince(dto.getProvince());
                existingUser.setDistrict(dto.getDistrict());
                existingUser.setSector(dto.getSector());
                existingUser.setImageUrl(dto.getImageUrl());
                existingUser.setNationalId(dto.getNationalId());

                // Save the updated user
                userRepository.save(existingUser);

                return ApiResponse.builder()
                        .success(true)
                        .data("User information updated successfully!")
                        .build();
            } else {
                System.out.println("The logged Id is " + loggedInUserId + " is not found");
                throw new NotFoundException("User not found!");
            }
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Internal server error ...");
        }
    }

    // Validate the input DTO
    private void validateUpdateDto(UserUpdateDto dto) {
        if (dto == null || dto.getName() == null || dto.getCell() == null
                || dto.getVillage() == null || dto.getPassword() == null
                || dto.getProvince() == null || dto.getDistrict() == null
                || dto.getSector() == null) {
            throw new BadRequestException("All update details are required!");
        }

    }

}
