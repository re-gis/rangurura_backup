package com.backend.proj.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.backend.proj.dtos.*;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.proj.entities.Otp;
import com.backend.proj.entities.User;
import com.backend.proj.enums.URole;
import com.backend.proj.exceptions.BadRequestException;
import com.backend.proj.exceptions.InvalidEnumConstantException;
import com.backend.proj.exceptions.JwtExpiredException;
import com.backend.proj.exceptions.MessageSendingException;
import com.backend.proj.exceptions.NotFoundException;
import com.backend.proj.exceptions.UnauthorisedException;
import com.backend.proj.repositories.OtpRepository;
import com.backend.proj.repositories.UserRepository;
import com.backend.proj.response.ApiResponse;
import com.backend.proj.response.NotFoundResponse;
import com.backend.proj.response.UserResponse;
import com.backend.proj.Services.UserService;
import com.backend.proj.utils.GetLoggedUser;
import com.backend.proj.utils.ValidateEnum;
import com.backend.proj.utils.Validators.Validator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final OtpServiceImpl otpServiceImpl;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final GetLoggedUser getLoggedUser;
    private final ValidateEnum validateEnum;

    @Override
    public ApiResponse<Object> registerUser(RegisterDto dto) throws Exception {
        try {
            if (dto.getName() == null || dto.getProvince() == null || dto.getDistrict() == null
                    || dto.getSector() == null || dto.getCell() == null || dto.getVillage() == null
                    || dto.getPassword() == null || dto.getCpassword() == null || dto.getNationalId() == null || dto.getPhoneNumber() == null) {
                throw new BadRequestException("All credentials are required!");
            } else {

                Validator.validatePhonePasswordAndNationalId(dto.getPhoneNumber(), dto.getPassword(), dto.getNationalId());

                if (!dto.getPassword().equals(dto.getCpassword())) {
                    return ApiResponse.builder()
                            .data("Confirm password to continue...")
                            .success(false)
                            .build();
                } else {
                    if (dto.getRole() != null) {
                        URole rl = URole.valueOf(dto.getRole().toUpperCase());
                        validateEnum.isValidEnumConstant(rl, URole.class);
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
                    System.out.println(dto.getRole());
                    user.setRole(URole.UMUTURAGE);
                    if (dto.getRole() != null) {
                        switch (dto.getRole()) {
                            case "umuyobozi", "UMUYOBOZI":
                                user.setRole(URole.UMUYOBOZI);
                                break;
                            case "admin", "ADMIN":
                                user.setRole(URole.ADMIN);
                                break;
                            case "umuturage", "UMUTURAGE":
                                user.setRole(URole.UMUTURAGE);
                                break;
                            default:
                                throw new BadRequestException("Role " + dto.getRole() + " not allowed!");
                        }
                    } else {
                        user.setRole(URole.UMUTURAGE);
                    }
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
                }
            }
        } catch (InvalidEnumConstantException | BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (MessageSendingException e) {
            throw new MessageSendingException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
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
            User euser = userRepository.findOneByPhone(eotp.get().getNumber())
                    .orElseThrow(() -> new NotFoundException("User not found!"));
            otpRepository.delete(otp);
            euser.setVerified(true);
            userRepository.save(euser);

            return ApiResponse.builder()
                    .success(true)
                    .data("Account verified successfully...")
                    .build();
        } catch (BadRequestException e) {
            throw new BadRequestException("Invalid OTP!");
        } catch (Exception e) {
            throw new Exception(e.getMessage());
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
            throw new Exception(e.getMessage());
        }
    }

    // //this is the function to find the id of the logged user

    private UUID getCurrentUserId() {
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

    // this is to get admins
    @Override
    public ApiResponse<Object> getAdmins() throws Exception {
        try {

            // find all leaders
            List<User> leaders = userRepository.findByRole(URole.valueOf("ADMIN"));

            // Check if the list is empty
            if (leaders.isEmpty()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message(
                                "No admins found in system!")
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            // You can further process the list of events as needed

            return ApiResponse.builder()
                    .data(leaders)
                    .success(true)
                    .build();
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            // e.printStackTrace();
            throw new Exception("Internal server error...");
        }
    }

    // this is to update the user details
    @Override
    public ApiResponse<Object> updateUser(UserUpdateDto dto) throws Exception {
        try {
            UUID loggedInUserId = getCurrentUserId();
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
            // e.printStackTrace();
            throw new Exception(e.getMessage());
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

    @Override
    public ApiResponse<Object> getAllUsers() throws Exception {
        try {
            UserResponse response = getLoggedUser.getLoggedUser();
            if (response.getRole() != URole.ADMIN) {
                throw new UnauthorisedException("You are not authorised to perform this action!");
            }

            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                NotFoundResponse response2 = NotFoundResponse.builder()
                        .message("No users found!")
                        .build();
                return ApiResponse.builder().success(true).data(response2).status(HttpStatus.OK).build();
            }

            List<UserResponse> responses = new ArrayList<>();
            for (User user : users) {
                UserResponse u = UserResponse.builder()
                        .name(user.getRealName())
                        .nationalId(user.getNationalId())
                        .province(user.getProvince())
                        .district(user.getDistrict())
                        .sector(user.getSector())
                        .cell(user.getCell())
                        .village(user.getVillage())
                        .phoneNumber(user.getPhone())
                        .role(user.getRole())
                        .isVerified(user.isVerified())
                        .build();

                responses.add(u);
            }

            return ApiResponse.builder()
                    .data(responses)
                    .success(true)
                    .status(HttpStatus.OK)
                    .build();
        } catch (JwtExpiredException e) {
            return ApiResponse.builder()
                    .error("JWT Expired")
                    .data("JWT token has expired. Please log in again.")
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        } catch (Exception e) {
            // System.out.println(e);
            throw new Exception(e.getMessage());
        }
    }

    public ApiResponse<Object> sendOtp(SendOtpDto dto) throws Exception {
        try {
            if (dto.getPhoneNumber() == null) {
                throw new BadRequestException("Please give the phone number used in registration!");
            }

            Optional<User> user = userRepository.findOneByPhone(dto.getPhoneNumber());
            if (!user.isPresent()) {
                throw new NotFoundException("User with phone: " + dto.getPhoneNumber() + " not found!");
            }

            // if number given send the otp
            String o = otpServiceImpl.generateOtp(6);
            System.out.println(o);
            String message = "Code yo guhindura ijambobanga  ni: " + o;
            otpServiceImpl.sendMessage(dto.getPhoneNumber(), message);

            Otp otp = new Otp();
            otp.setNumber(dto.getPhoneNumber());
            otp.setOtp(passwordEncoder.encode(o));
            otpRepository.save(otp);
            return ApiResponse.builder()
                    .data("OTP sent successfully!")
                    .success(true)
                    .build();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public boolean verifyOtp(String otp, String phone) throws Exception {
        try {
            if (otp == null) {
                throw new BadRequestException("Please provide the otp sent to your number!");
            }

            if (phone == null) {
                throw new BadRequestException("Please provide the phone number!");
            }

            Optional<Otp> eOtp = otpRepository.findOneByNumber(phone);
            if (eOtp == null || !passwordEncoder.matches(otp, eOtp.get().getOtp())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


    public ApiResponse<Object> resetPassword(ResetPasswordDto dto) throws Exception {
        try {
            if (!verifyOtp(dto.getOtp(), dto.getPhone())) {
                return ApiResponse.builder()
                        .data("Invalid OTP provided, check your phone and if not sent ask for another one!")
                        .success(false)
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
            } else {
                if (dto.getNewPassword() == null) {
                    throw new BadRequestException("Please provide the new password...");
                } else {
                    Optional<User> user = userRepository.findOneByPhone(dto.getPhone());
                    if (user == null) {
                        throw new NotFoundException("User with phone: " + dto.getPhone() + " not found!");
                    }

                    user.get().setPassword(passwordEncoder.encode(dto.getNewPassword()));
                    // delete the otp
                    Optional<Otp> otp = otpRepository.findOneByNumber(dto.getPhone());
                    if (otp == null) {
                        throw new BadRequestException(
                                "Invalid OTP provided, check your phone and if not sent ask for another one!");
                    }
                    otpRepository.delete(otp.get());
                    userRepository.save(user.get());
                    return ApiResponse.builder()
                            .data("Password reset successfully...")
                            .success(true)
                            .status(HttpStatus.OK)
                            .build();
                }
            }

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ApiResponse<Object> resendOtp(String phone) throws Exception {
        try {
            if (phone == null) {
                throw new BadRequestException("Phone number required to send otp...");
            } else {
                // first check the otp and delete it
                Optional<Otp> eOtp = otpRepository.findOneByNumber(phone);
                if (eOtp.isPresent()) {
                    otpRepository.delete(eOtp.get());
                }
                // send the message
                String o = otpServiceImpl.generateOtp(6);
                System.out.println(o);
                String message = "Your verification code to RANGURURA is: " + o;
                otpServiceImpl.sendMessage(phone, message);
                Otp otp = new Otp();
                otp.setNumber(phone);
                otp.setOtp(passwordEncoder.encode(o));
                otpRepository.save(otp);

                return ApiResponse.builder()
                        .data("Otp resent successfully")
                        .success(true)
                        .status(HttpStatus.OK).build();
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ApiResponse<Object> getUserByNationalId(GetUserByNationalIdDto dto) throws Exception {

        try {
            UserResponse response = getLoggedUser.getLoggedUser();
            if(response.getRole()!=URole.UMUYOBOZI && response.getRole()!=URole.ADMIN){
                throw new Exception("You are not allowed to perfom this action!");
            }
            Optional<User>  user=userRepository.findByNationalId(dto.getNationalId());
            if(user.isPresent()){
                return  ApiResponse.builder()
                        .data(user)
                        .success(true)
                        .status(HttpStatus.OK).build();

            }
            return ApiResponse.builder()
                    .data("The user  with "  + dto.getNationalId() + " is not found!" )
                    .success(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .build();

        }catch (Exception e){
            throw new Exception(e.getMessage());


        }

    }

    @Override
    public ApiResponse<Object> getUserById(UUID userId) throws Exception {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                // If user is found, return success response with user data
                return ApiResponse.builder()
                        .data(user.get()) // Unwrap Optional<User> to User
                        .status(HttpStatus.OK)
                        .build();
            } else {
                // If user is not found, return failure response with message
                return ApiResponse.builder()
                        .data("I can't find the user with that UID")
                        .status(HttpStatus.NOT_FOUND) // Change status to NOT_FOUND or BAD_REQUEST as per your requirement
                        .build();
            }
        } catch (Exception e) {
            // If any exception occurs, rethrow it
            throw new Exception(e.getMessage());
        }
    }


}
