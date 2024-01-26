package com.backend.rangurura.serviceImpl;

import com.backend.rangurura.Services.SuggestionService;
import com.backend.rangurura.dtos.SuggestionDto;
import com.backend.rangurura.dtos.SuggestionUpdateDto;
import com.backend.rangurura.entities.Leaders;
import com.backend.rangurura.entities.Suggestions;
import com.backend.rangurura.enums.ESuggestion;
import com.backend.rangurura.enums.EUrwego;
import com.backend.rangurura.enums.URole;
import com.backend.rangurura.exceptions.BadRequestException;
import com.backend.rangurura.exceptions.NotFoundException;
import com.backend.rangurura.exceptions.ServiceException;
import com.backend.rangurura.exceptions.UnauthorisedException;
import com.backend.rangurura.repositories.LeaderRepository;
import com.backend.rangurura.repositories.SuggestionRepository;
import com.backend.rangurura.response.ApiResponse;
import com.backend.rangurura.response.SuggestionResponse;
import com.backend.rangurura.response.UserResponse;
import com.backend.rangurura.utils.GetLoggedUser;
import java.util.*;

import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SuggestionServiceImpl implements SuggestionService {
    private final SuggestionRepository suggestionRepository;
    private final GetLoggedUser getLoggedUser;
    private final LeaderRepository leaderRepository;

    @Override
    public ApiResponse<Object> PostSuggestion(SuggestionDto dto) throws Exception {
        try {
            if (dto.getCategory() == null || dto.getIgitekerezo() == null || dto.getNationalId() == null
                    || dto.getUrwego() == null || dto.getLocation() == null) {
                throw new BadRequestException("All suggestion details are required!");
            }

            // Convert DTO to entity
            Suggestions suggestionEntity = convertDtoToEntity(dto);

            // Save the suggestion to the repository
            Suggestions savedSuggestion = suggestionRepository.save(suggestionEntity);
            if (savedSuggestion != null) {
                SuggestionResponse response = new SuggestionResponse();
                response.setMessage("Suggestion sent successfully");
                response.setSuggestion(savedSuggestion);
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            } else {
                throw new ServiceException("Failed to save the Suggestion!");
            }
        } catch (ServiceException e) {
            throw new ServiceException(e.getMessage());
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            throw new Exception("Internal server error...");
        }
    }

    @Override
    public ApiResponse<Object> UpdateSuggestion(SuggestionUpdateDto dto) throws Exception {
        try {

        } catch (Exception e) {
            throw new Exception("Internal server error...");
        }
        return null;
    }

    @Override
    public ApiResponse<Object> getAllMySuggestions() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            String nationalId = user.getNationalId();

            // the nationalid is like the owner of the suggestion
            List<Suggestions> suggestions = suggestionRepository.findAllByNationalId(nationalId);
            if (suggestions.isEmpty()) {
                throw new NotFoundException("No suggestions found for user: " + nationalId);
            }

            return ApiResponse.builder()
                    .data(suggestions)
                    .success(true)
                    .build();

        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new Exception("Internal server error...");
        }
    }

    @Override
    public ApiResponse<Object> getSuggestionsByStatus(ESuggestion status) throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            if (user.getRole() != URole.UMUYOBOZI) {
                throw new UnauthorisedException("You are not authorised to perform this action!");
            }

            Optional<Leaders> leader = leaderRepository.findByNationalId(user.getNationalId());
            if (!leader.isPresent()) {
                throw new NotFoundException("Leader " + user.getNationalId() + " not found!");
            }

            // get the suggestions zaho ayoboye
            List<Suggestions> suggestions = suggestionRepository.findAllByUrwegoAndLocationAndCategory(
                    leader.get().getOriganizationLevel(), leader.get().getLocation(), leader.get().getCategory());

            if (suggestions.isEmpty()) {
                throw new NotFoundException(String.format("No suggestions found in %s and category: %s",
                        leader.get().getLocation(), leader.get().getCategory()));
            }

            List<Suggestions> filteredSuggestions = suggestions.stream().filter(sugg -> sugg.getStatus() == status)
                    .collect(Collectors.toList());

            if (filteredSuggestions.isEmpty()) {
                throw new NotFoundException("No " + status + " suggestions found!");
            }

            return ApiResponse.builder()
                    .data(filteredSuggestions)
                    .success(true)
                    .build();

        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ApiResponse<Object> getMyLocalSuggestions() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            if (user.getRole() != URole.UMUYOBOZI) {
                throw new UnauthorisedException("You are not authorised to perform this action!");
            }

            Optional<Leaders> leader = leaderRepository.findByNationalId(user.getNationalId());
            if (!leader.isPresent()) {
                throw new NotFoundException("Leader " + user.getNationalId() + " not found!");
            }

            // get the suggestions zaho ayoboye
            List<Suggestions> suggestions = suggestionRepository.findAllByUrwegoAndLocationAndCategory(
                    leader.get().getOriganizationLevel(), leader.get().getLocation(), leader.get().getCategory());

            if (suggestions.isEmpty()) {
                throw new NotFoundException(String.format("No suggestions found in %s and category: %s",
                        leader.get().getLocation(), leader.get().getCategory()));
            }

            return ApiResponse.builder()
                    .data(suggestions)
                    .success(true)
                    .build();
        } catch (UnauthorisedException e) {
            throw new UnauthorisedException(e.getMessage());
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /*
     * @desciption
     * this is to get the Id of the current suggestion
     * 
     */
    private Suggestions convertDtoToEntity(SuggestionDto dto) {

        // Implement logic to convert DTO to Entity
        Suggestions suggestions = new Suggestions();

        suggestions.setUrwego(dto.getUrwego());
        suggestions.setIgitekerezo(dto.getIgitekerezo());
        suggestions.setCategory(dto.getCategory());
        suggestions.setStatus(ESuggestion.PENDING);

        EUrwego urwego = dto.getUrwego();
        if (urwego == EUrwego.AKARERE || urwego == EUrwego.INTARA) {
            suggestions.setUpperLevel("none");
        } else {
            if (dto.getUpperLevel() == null) {
                EUrwego missingUrwego = null;
                switch (urwego) {
                    case AKAGARI:
                        missingUrwego = EUrwego.UMURENGE;
                        break;
                    case UMURENGE:
                        missingUrwego = EUrwego.AKAGARI;
                        break;
                    case UMUDUGUDU:
                        missingUrwego = EUrwego.AKAGARI;
                        break;
                    default:
                        throw new BadRequestException(String.format("%s level is not found!", urwego));
                }
                throw new BadRequestException("Which " + missingUrwego + " is " + dto.getLocation() + " located!");
            }
            suggestions.setUpperLevel(dto.getUpperLevel());
        }

        suggestions.setNationalId(dto.getNationalId());
        suggestions.setLocation(dto.getLocation());
        if (dto.getPhoneNumber() != null) {
            suggestions.setPhoneNumber(dto.getPhoneNumber());
        } else {
            suggestions.setPhoneNumber("none");
        }

        return suggestions;
    }
}
