package com.backend.rangurura.serviceImpl;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.rangurura.dtos.RegisterDto;
import com.backend.rangurura.dtos.VerifyOtpDto;
import com.backend.rangurura.entities.Otp;
import com.backend.rangurura.entities.User;
import com.backend.rangurura.exceptions.BadRequestException;
import com.backend.rangurura.exceptions.MessageSendingException;
import com.backend.rangurura.exceptions.NotFoundException;
import com.backend.rangurura.exceptions.UnauthorisedException;
import com.backend.rangurura.repositories.OtpRepository;
import com.backend.rangurura.repositories.UserRepository;
import com.backend.rangurura.response.ApiResponse;
import com.backend.rangurura.services.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final OtpServiceImpl otpServiceImpl;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;

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
                        .data("Indangamuntu cyangwa numero yawe isanzwe muri Rangurura yihindure wongere ugerageze cyangwa winjire...")
                        .success(false)
                        .build();
            }

            // send the message
            String o = otpServiceImpl.generateOtp(6);
            System.out.println(o);
            String message = "Your verification code to RANGURURA is: " + o;
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
                    .data("Urakoze kwiyandikisha muri Rangurura! Ubu ushobora kwinjiramo ugatanga ikibazo cyawe!")
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
            return ApiResponse.builder()
                    .data(user.get().getNationalId())
                    .success(true)
                    .build();
        } catch (NotFoundException e) {
            throw new NotFoundException("User not found!");
        } catch (Exception e) {
            throw new Exception("Internal server error...");
        }
    }

}
