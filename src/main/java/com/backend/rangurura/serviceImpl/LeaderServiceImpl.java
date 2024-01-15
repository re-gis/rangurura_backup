package com.backend.rangurura.serviceImpl;

import com.backend.rangurura.Services.LeaderService;
import com.backend.rangurura.dtos.RegisterLeaderDto;
import com.backend.rangurura.entities.Leaders;
import com.backend.rangurura.enums.ECategory;
import com.backend.rangurura.enums.EUrwego;
import com.backend.rangurura.enums.URole;
import com.backend.rangurura.exceptions.NotFoundException;
import com.backend.rangurura.exceptions.UnauthorisedException;
import com.backend.rangurura.repositories.LeaderRepository;
import com.backend.rangurura.response.ApiResponse;
import com.backend.rangurura.response.UserResponse;
import com.backend.rangurura.utils.GetLoggedUser;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LeaderServiceImpl implements LeaderService {
    private final LeaderRepository leaderRepository;
    private final GetLoggedUser getLoggedUser;

    @Override
    public ApiResponse<Object> registerNewLeader(@Valid RegisterLeaderDto dto) throws Exception {
        try {
            // before registering the leader you must be a leader of a higher level
            UserResponse user = getLoggedUser.getLoggedUser();

            if (user.getRole() != URole.UMUYOBOZI) {
                throw new UnauthorisedException("You are not allowed to perform this action...");
            }

            Leaders loggedLeader = leaderRepository.findByNationalId(user.getNationalId())
                    .orElseThrow(() -> new NotFoundException("Leader not found!"));

            EUrwego urwego = dto.getOrganizationLevel();
            Leaders savedLeader = null;
            switch (urwego) {
                case AKAGARI:
                    // check if the leader's akagari = the dto's akagari
                    if (dto.getLocation() == user.getCell()
                            && (loggedLeader.getOriganizationLevel() == EUrwego.UMURENGE
                                    || loggedLeader.getOriganizationLevel() == EUrwego.AKAGARI)
                            && loggedLeader.getCategory() == ECategory.IMIYOBORERE) {
                        // register the leader
                        // this is to convert DTO to entity
                        Leaders leadersEntity = convertDtoToEntity(dto);

                        savedLeader = leaderRepository.save(leadersEntity);
                    }
                    break;
                case AKARERE:
                    // check if the leader's akarere = the dto's akarere
                    if (dto.getLocation() == user.getDistrict()
                            && (loggedLeader.getOriganizationLevel() == EUrwego.INTARA
                                    || loggedLeader.getOriganizationLevel() == EUrwego.AKARERE)
                            && loggedLeader.getCategory() == ECategory.IMIYOBORERE) {
                        // register the leader
                        // this is to convert DTO to entity
                        Leaders leadersEntity = convertDtoToEntity(dto);

                        savedLeader = leaderRepository.save(leadersEntity);

                    }
                    break;
                case INTARA:
                    // check if the leader's intara = the dto's intara
                    if (dto.getLocation() == user.getProvince()
                            && (loggedLeader.getOriganizationLevel() == EUrwego.INTARA
                                    || user.getRole() == URole.ADMIN)
                            && loggedLeader.getCategory() == ECategory.IMIYOBORERE) {
                        // register the leader
                        // this is to convert DTO to entity
                        Leaders leadersEntity = convertDtoToEntity(dto);

                        savedLeader = leaderRepository.save(leadersEntity);

                    }
                    break;
                case UMUDUGUDU:
                    // check if the leader's umudugudu = the dto's umudugudu
                    if (dto.getLocation() == user.getVillage()
                            && (loggedLeader.getOriganizationLevel() == EUrwego.AKAGARI
                                    || loggedLeader.getOriganizationLevel() == EUrwego.UMUDUGUDU)
                            && loggedLeader.getCategory() == ECategory.IMIYOBORERE) {
                        // register the leader
                        // this is to convert DTO to entity
                        Leaders leadersEntity = convertDtoToEntity(dto);

                        savedLeader = leaderRepository.save(leadersEntity);

                    }
                    break;
                case UMURENGE:
                    // check if the leader's umurenge = the dto's umurenge
                    if (dto.getLocation() == user.getDistrict()
                            && (loggedLeader.getOriganizationLevel() == EUrwego.UMURENGE
                                    || loggedLeader.getOriganizationLevel() == EUrwego.AKARERE)
                            && loggedLeader.getCategory() == ECategory.IMIYOBORERE) {
                        // register the leader
                        // this is to convert DTO to entity
                        Leaders leadersEntity = convertDtoToEntity(dto);

                        savedLeader = leaderRepository.save(leadersEntity);

                    }
                    break;
                default:
                    throw new Exception("Unknown Organisational level provided!");
            }
            if (savedLeader != null) {
                return ApiResponse.builder()
                        .data("Leader is added successfully!")
                        .success(true)
                        .build();
            } else {
                return ApiResponse.builder()
                        .data("Failed to add leader to position")
                        .success(false)
                        .build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Failed to add new leader to the system please try again!");
        }

    }

    // this is the function to convert dto to entity
    private Leaders convertDtoToEntity(RegisterLeaderDto dto) {

        // Implement logic to convert DTO to Entity
        Leaders leaders = new Leaders();

        leaders.setNationalId(dto.getNationalId());
        leaders.setLocation(dto.getLocation());
        leaders.setCategory(dto.getCategory());
        leaders.setOriganizationLevel(dto.getOrganizationLevel());
        leaders.setVerified(false);
        leaders.setRole(URole.UMUYOBOZI);

        return leaders;
    }

}
