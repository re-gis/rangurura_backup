package com.backend.proj.serviceImpl;

import com.backend.proj.Services.LeaderService;
import com.backend.proj.dtos.RegisterLeaderDto;
import com.backend.proj.dtos.UpdateLeaderDto;
import com.backend.proj.dtos.UserLeaderDto;
import com.backend.proj.entities.Leaders;
import com.backend.proj.entities.Otp;
import com.backend.proj.entities.User;
import com.backend.proj.enums.URole;
import com.backend.proj.exceptions.BadRequestException;
import com.backend.proj.exceptions.NotFoundException;
import com.backend.proj.exceptions.UnauthorisedException;
import com.backend.proj.repositories.LeaderRepository;
import com.backend.proj.repositories.OtpRepository;
import com.backend.proj.repositories.UserRepository;
import com.backend.proj.response.ApiResponse;
import com.backend.proj.response.NotFoundResponse;
import com.backend.proj.response.UserResponse;
import com.backend.proj.utils.GetLoggedUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LeaderServiceImpl implements LeaderService {
    private final LeaderRepository leaderRepository;
    private final GetLoggedUser getLoggedUser;
    private final OtpServiceImpl otpServiceImpl;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpRepository otpRepository;

    // @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ApiResponse<Object> registerNewLeader(RegisterLeaderDto dto) throws Exception {
        try {
            UserResponse userResponse = getLoggedUser.getLoggedUser();
            if (userResponse.getRole() != URole.ADMIN && userResponse.getRole() != URole.UMUYOBOZI) {
                throw new UnauthorisedException("You are not allowed to perform this action!");
            }

            if (!userResponse.isVerified()) {
                throw new UnauthorisedException("Verify the account to continue please!");
            }
            Leaders savedLeader = null;
            Optional<User> euser = userRepository.findByNationalId(dto.getNationalId());
            if (euser.isPresent()) {
                // we create a leader and update the user.ROLE
                Leaders leaderEntity = convertDtoToEntity(dto);
                // save the leader and update the role of the user
                euser.get().setRole(URole.UMUYOBOZI);
                savedLeader = leaderRepository.save(leaderEntity);
                userRepository.save(euser.get());
                if (savedLeader == null) {
                    throw new Exception("Error while saving the user...");
                }
            } else {
                // there is no user so create the user and leader
                // send the message
                if (dto.getName() == null || dto.getCell() == null || dto.getDistrict() == null
                        || dto.getProvince() == null || dto.getVillage() == null || dto.getSector() == null) {
                    throw new BadRequestException(
                            "Since the leader is new to system, your all credentials and location information...");
                }
                String o = otpServiceImpl.generateOtp(6);
                String message = "Your verification code to proj is: " + o
                        + "\n and you are now registered as a leader of " + dto.getLocation();
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
                user.setPassword(passwordEncoder.encode(dto.getNationalId()));
                user.setProvince(dto.getProvince());
                user.setDistrict(dto.getDistrict());
                user.setSector(dto.getSector());
                user.setImageUrl("https://icon-library.com/images/no-user-image-icon/no-user-image-icon-0.jpg");
                user.setVerified(false);
                user.setRole(URole.UMUYOBOZI);

                otpRepository.save(otp);
                userRepository.save(user);

                Leaders leaderEntity = convertDtoToEntity(dto);
                savedLeader = leaderRepository.save(leaderEntity);
                if (savedLeader == null) {
                    throw new Exception("Error while saving the leader...");
                }

            }

            return ApiResponse.builder()
                    .data("Leader successfully registered, verify to continue to system... ")
                    .success(true)
                    .build();
        } catch (UnauthorisedException e) {
            throw new UnauthorisedException(e.getMessage());
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    // this is to get all leaders

    @Override
    public ApiResponse<Object> getLeaders() throws Exception {
        try {
            // Perform the JOIN operation using a custom query
            List<Object[]> userLeaderPairs = userRepository.findAllUsersAndLeaders();

            // Check if the list is empty
            if (userLeaderPairs.isEmpty()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("No leaders found!")
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            // Process the list of pairs to extract leaders and users
            List<UserLeaderDto> userLeaderDtos = new ArrayList<>();
            for (Object[] pair : userLeaderPairs) {
                User user = (User) pair[0];
                Leaders leader = (Leaders) pair[1];

                // Create a DTO object to hold combined data from User and Leader
                UserLeaderDto dto = new UserLeaderDto();
                dto.setUser(user);
                dto.setLeader(leader);

                userLeaderDtos.add(dto);
            }

            // You can further process the list of userLeaderDtos as needed

            return ApiResponse.builder()
                    .data(userLeaderDtos)
                    .success(true)
                    .build();
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    // this is to delete the leader
    @Override
    public ApiResponse<Object> deleteLeader(UUID id) throws Exception {
        try {
            UserResponse userResponse = getLoggedUser.getLoggedUser();

            // Check if the leader ID is provided
            if (id == null) {
                throw new BadRequestException("Leader ID is required!");
            }

            // Check if the user has the required role
            if (userResponse.getRole() != URole.ADMIN && userResponse.getRole() != URole.UMUYOBOZI) {
                throw new UnauthorisedException("You are not allowed to perform this action!");
            }

            // Retrieve the leader by ID
            Optional<Leaders> optionalLeader = leaderRepository.findById(id);

            if (optionalLeader.isEmpty()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("Leader not found with ID: "
                                + id)
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            Leaders leader = optionalLeader.get();
            Optional<User> optionalUser = userRepository.findByNationalId(leader.getNationalId());
            if (optionalUser.isEmpty()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("User not found with national ID: " + leader
                                .getNationalId())
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }
            User user = optionalUser.get();
            user.setRole(URole.UMUTURAGE);
            // Save the updated user
            userRepository.save(user);

            // Delete the leader from the database
            leaderRepository.delete(leader);

            return ApiResponse.builder()
                    .data(leader + " Was deleted successfully! ")
                    .success(true)
                    .build();
        } catch (BadRequestException | NotFoundException | UnauthorisedException e) {
            throw e; // Re-throw the known exceptions
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ApiResponse<Object> updateLeader(UpdateLeaderDto dto, UUID id) throws Exception {
        try {
            UserResponse userResponse = getLoggedUser.getLoggedUser();

            // Check if the ID is provided
            if (id == null) {
                throw new BadRequestException("Leader ID is required!");
            }

            // Check if the user has the required role
            if (userResponse.getRole() != URole.ADMIN && userResponse.getRole() != URole.UMUYOBOZI) {
                throw new UnauthorisedException("You are not allowed to perform this action!");
            }

            // Retrieve the leader by ID
            Optional<Leaders> optionalLeader = leaderRepository.findById(id);
            if (optionalLeader.isEmpty()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("Leader not found with ID: "
                                + id)
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }
            Leaders leader = optionalLeader.get();

            // Retrieve the user by nationalId
            Optional<User> optionalUser = userRepository.findByNationalId(leader.getNationalId());
            if (optionalUser.isEmpty()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("User not found with national ID: " + leader
                                .getNationalId())
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }
            User user = optionalUser.get();

            // Update the nationalId in the User entity
            user.setNationalId(dto.getNationalId());
            // Save the updated user
            userRepository.save(user);

            leader.setNationalId(dto.getNationalId());
            leader.setOrganizationLevel(dto.getOrganizationLevel());
            leader.setLocation(dto.getLocation());
            leader.setCategory(dto.getCategory());
            // Save the updated leader
            leaderRepository.save(leader);

            return ApiResponse.builder()
                    .data(leader + " Was deleted successfully!")
                    .success(true)
                    .build();
        } catch (BadRequestException | NotFoundException | UnauthorisedException e) {
            throw e; // Re-throw the known exceptions
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    // this is the function to convert dto to entity
    private Leaders convertDtoToEntity(RegisterLeaderDto dto) {
        // Implement logic to convert DTO to Entity
        Leaders leaders = new Leaders();

        leaders.setNationalId(dto.getNationalId());
        leaders.setLocation(dto.getLocation());
        leaders.setCategory(dto.getCategory());
        leaders.setOrganizationLevel(dto.getOrganizationLevel());
        leaders.setVerified(false);
        leaders.setRole(URole.UMUYOBOZI);
        // leaders.setPhoneNumber(dto.getPhoneNumber());

        return leaders;
    }

    @Override
    public ApiResponse<Object> getLeaderById(UUID id) throws Exception {
        try {
            Optional<Leaders> leader = leaderRepository.findById(id);
            if (leader.isEmpty()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("Leader " + id + " not found!")
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .status(HttpStatus.OK)
                        .success(true)
                        .build();
            }

            return ApiResponse.builder()
                    .success(true)
                    .data(leader)
                    .status(HttpStatus.OK)
                    .build();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

}
