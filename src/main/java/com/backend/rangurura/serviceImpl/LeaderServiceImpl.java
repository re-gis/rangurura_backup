package com.backend.rangurura.serviceImpl;

import com.backend.rangurura.Services.LeaderService;
import com.backend.rangurura.dtos.RegisterLeaderDto;
import com.backend.rangurura.dtos.SuggestionDto;
import com.backend.rangurura.entities.Leaders;
import com.backend.rangurura.entities.Suggestions;
import com.backend.rangurura.repositories.LeaderRepository;
import com.backend.rangurura.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LeaderServiceImpl implements LeaderService {
    private final LeaderRepository leaderRepository;

    @Override
    public ApiResponse<Object> registerNewLeader(@Valid RegisterLeaderDto dto) throws Exception {
        try {
            // this is to convert DTO to entity
            Leaders leadersEntity = convertDtoToEntity(dto);

            Leaders savedLeader = leaderRepository.save(leadersEntity);
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

        return leaders;
    }

}
