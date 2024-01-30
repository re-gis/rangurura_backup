package com.backend.proj.serviceImpl;

import com.backend.proj.Services.LeaderService;
import com.backend.proj.dtos.RegisterLeaderDto;
import com.backend.proj.entities.Leaders;
import com.backend.proj.entities.Otp;
import com.backend.proj.entities.User;
import com.backend.proj.enums.ECategory;
import com.backend.proj.enums.EUrwego;
import com.backend.proj.enums.URole;
import com.backend.proj.exceptions.BadRequestException;
import com.backend.proj.exceptions.NotFoundException;
import com.backend.proj.exceptions.UnauthorisedException;
import com.backend.proj.repositories.LeaderRepository;
import com.backend.proj.repositories.OtpRepository;
import com.backend.proj.repositories.UserRepository;
import com.backend.proj.response.ApiResponse;
import com.backend.proj.response.UserResponse;
import com.backend.proj.utils.GetLoggedUser;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ApiResponse<Object> registerNewLeader(RegisterLeaderDto dto) throws Exception {
        try {
            UserResponse userResponse = getLoggedUser.getLoggedUser();
            if (userResponse.getRole() != URole.ADMIN) {
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
                    throw new Exception("Internal server error...");
                }
            } else {
                // there is no user so create the user and leader
                // send the message
                if (dto.getName() == null || dto.getCell() == null || dto.getDistrict() == null
                        || dto.getProvince() == null || dto.getVillage() == null || dto.getSector() == null) {
                    throw new BadRequestException(
                            "Since the leader is new to proj, your all credentials and location information...");
                }
                String o = otpServiceImpl.generateOtp(6);
                System.out.println(o);
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
                    throw new Exception("Internal server error...");
                }

            }

            return ApiResponse.builder()
                    .data("Leader successfully registered, verify to continue to proj... \n Password is the given national id")
                    .success(true)
                    .build();
        } catch (UnauthorisedException e) {
            throw new UnauthorisedException(e.getMessage());
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            throw new Exception("Internal server error...");
        }
    }

    @Override
    public ApiResponse<Object> getLocalLeaders() throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLocalLeaders'");
    }

    @Override
    public ApiResponse<Object> getLeaders() throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLeaders'");
    }

    // @Override
    // public ApiResponse<Object> registerNewLeader(@Valid RegisterLeaderDto dto)
    // throws Exception {
    // try {
    // // before registering the leader you must be a leader of a higher level
    // UserResponse user = getLoggedUser.getLoggedUser();

    // if (user.getRole() != URole.UMUYOBOZI) {
    // throw new UnauthorisedException("You are not allowed to perform this
    // action...");
    // }

    // Leaders loggedLeader =
    // leaderRepository.findByNationalId(user.getNationalId())
    // .orElseThrow(() -> new NotFoundException("Leader not found!"));

    // EUrwego urwego = dto.getOrganizationLevel();
    // Leaders savedLeader = null;
    // switch (urwego) {
    // case AKAGARI:
    // // check if the leader's akagari = the dto's akagari
    // if ((loggedLeader.getOriganizationLevel() == EUrwego.UMURENGE
    // || loggedLeader.getOriganizationLevel() == EUrwego.AKAGARI)
    // && loggedLeader.getCategory() == ECategory.IMIYOBORERE) {
    // // register the leader
    // // this is to convert DTO to entity
    // Leaders leadersEntity = convertDtoToEntity(dto);

    // savedLeader = leaderRepository.save(leadersEntity);
    // }
    // break;
    // case AKARERE:
    // // check if the leader's akarere = the dto's akarere
    // if ((loggedLeader.getOriganizationLevel() == EUrwego.INTARA
    // || loggedLeader.getOriganizationLevel() == EUrwego.AKARERE)
    // && loggedLeader.getCategory() == ECategory.IMIYOBORERE) {
    // // register the leader
    // // this is to convert DTO to entity
    // Leaders leadersEntity = convertDtoToEntity(dto);

    // savedLeader = leaderRepository.save(leadersEntity);

    // }
    // break;
    // case INTARA:
    // // check if the leader's intara = the dto's intara
    // if (dto.getLocation() == user.getProvince()
    // && (loggedLeader.getOriganizationLevel() == EUrwego.INTARA
    // || user.getRole() == URole.ADMIN)
    // && loggedLeader.getCategory() == ECategory.IMIYOBORERE) {
    // // register the leader
    // // this is to convert DTO to entity
    // Leaders leadersEntity = convertDtoToEntity(dto);

    // savedLeader = leaderRepository.save(leadersEntity);

    // }
    // break;
    // case UMUDUGUDU:
    // // check if the leader's umudugudu = the dto's umudugudu
    // if (dto.getLocation() == user.getVillage()
    // && (loggedLeader.getOriganizationLevel() == EUrwego.AKAGARI
    // || loggedLeader.getOriganizationLevel() == EUrwego.UMUDUGUDU)
    // && loggedLeader.getCategory() == ECategory.IMIYOBORERE) {
    // // register the leader
    // // this is to convert DTO to entity
    // Leaders leadersEntity = convertDtoToEntity(dto);

    // savedLeader = leaderRepository.save(leadersEntity);

    // }
    // break;
    // case UMURENGE:
    // // check if the leader's umurenge = the dto's umurenge
    // if (dto.getLocation() == user.getDistrict()
    // && (loggedLeader.getOriganizationLevel() == EUrwego.UMURENGE
    // || loggedLeader.getOriganizationLevel() == EUrwego.AKARERE)
    // && loggedLeader.getCategory() == ECategory.IMIYOBORERE) {
    // // register the leader
    // // this is to convert DTO to entity
    // Leaders leadersEntity = convertDtoToEntity(dto);

    // savedLeader = leaderRepository.save(leadersEntity);

    // }
    // break;
    // default:
    // throw new Exception("Unknown Organisational level provided!");
    // }
    // if (savedLeader != null) {
    // // send an sms to verify
    // String message = String.format("Uri umuyobozi w'%s \n Bikozwe na %s \n numero
    // ya telephone: %s",
    // savedLeader.getLocation(), user.getName(), user.getPhoneNumber());

    // otpServiceImpl.sendMessage(dto.getPhoneNumber(), message);
    // return ApiResponse.builder()
    // .data("Leader is added successfully!")
    // .success(true)
    // .build();
    // } else {
    // return ApiResponse.builder()
    // .data("Failed to add leader to position")
    // .success(false)
    // .build();
    // }

    // } catch (NotFoundException e) {
    // throw new NotFoundException(e.getMessage());
    // } catch (UnauthorisedException e) {
    // throw new UnauthorisedException(e.getMessage());
    // } catch (Exception e) {
    // e.printStackTrace();
    // throw new Exception("Failed to add new leader to the system please try
    // again!");
    // }

    // }

    // @PreAuthorize("hasRole('UMUYOBOZI')")
    // @Override
    // public ApiResponse<Object> getLocalLeaders() throws Exception {
    // try {
    // UserResponse user = getLoggedUser.getLoggedUser();
    // if (user.getRole() != URole.UMUYOBOZI) {
    // throw new UnauthorisedException("You are not allowed to perform this
    // action!");
    // }
    // // get the leader
    // Optional<Leaders> leader =
    // leaderRepository.findByNationalId(user.getNationalId());
    // if (!leader.isPresent()) {
    // throw new NotFoundException("Leader " + user.getNationalId() + " not
    // found!");
    // }

    // List<Leaders> allLeaders =
    // leaderRepository.findAllByLocationAndOriganizationLevel(
    // leader.get().getLocation(), leader.get().getOriganizationLevel());
    // if (allLeaders.isEmpty()) {
    // throw new NotFoundException("No leaders found!");
    // }

    // // return those leaders in the same location and same level
    // return ApiResponse.builder()
    // .data(allLeaders)
    // .success(true)
    // .build();

    // } catch (NotFoundException e) {
    // throw new NotFoundException(e.getMessage());
    // } catch (UnauthorisedException e) {
    // throw new UnauthorisedException(e.getMessage());
    // } catch (Exception e) {
    // throw new Exception("Internal server error...");
    // }
    // }

    // @PreAuthorize("hasRole('UMUYOBOZI')")
    // @Override
    // public ApiResponse<Object> getLeaders() throws Exception {
    // try {
    // UserResponse user = getLoggedUser.getLoggedUser();
    // if (user.getRole() != URole.UMUYOBOZI) {
    // throw new UnauthorisedException("You are not allowed to perform this
    // action!");
    // }

    // } catch (Exception e) {
    // throw new Exception("Internal server error...");
    // }

    // return null;
    // }

    // this is the function to convert dto to entity
    private Leaders convertDtoToEntity(RegisterLeaderDto dto) {

        // // check if the leader existed as a user
        // Optional<User> user = userRepository.findByNationalId(dto.getNationalId());
        // if (user.isPresent()) {
        // user.get().setRole(URole.UMUYOBOZI);
        // // save the user
        // userRepository.save(user.get());
        // }

        // Implement logic to convert DTO to Entity
        Leaders leaders = new Leaders();

        leaders.setNationalId(dto.getNationalId());
        leaders.setLocation(dto.getLocation());
        leaders.setCategory(dto.getCategory());
        leaders.setOriganizationLevel(dto.getOrganizationLevel());
        leaders.setVerified(false);
        leaders.setRole(URole.UMUYOBOZI);
        leaders.setPhoneNumber(dto.getPhoneNumber());

        return leaders;
    }

}
