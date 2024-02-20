package com.backend.proj.serviceImpl;

import com.backend.proj.Services.SuggestionService;
import com.backend.proj.dtos.SuggestionDto;
import com.backend.proj.dtos.SuggestionUpdateDto;
import com.backend.proj.entities.Leaders;
import com.backend.proj.entities.Suggestions;
import com.backend.proj.enums.ESuggestion;
import com.backend.proj.enums.EUrwego;
import com.backend.proj.enums.URole;
import com.backend.proj.exceptions.BadRequestException;
import com.backend.proj.exceptions.NotFoundException;
import com.backend.proj.exceptions.ServiceException;
import com.backend.proj.exceptions.UnauthorisedException;
import com.backend.proj.repositories.LeaderRepository;
import com.backend.proj.repositories.SuggestionRepository;
import com.backend.proj.response.ApiResponse;
import com.backend.proj.response.NotFoundResponse;
import com.backend.proj.response.SuggestionResponse;
import com.backend.proj.response.UserResponse;
import com.backend.proj.utils.GetLoggedUser;
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
    public ApiResponse<Object> UpdateSuggestion(SuggestionUpdateDto dto, Long id) throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            Optional<Suggestions> existingSuggestionOptional = suggestionRepository.findById(id);
            if (!existingSuggestionOptional.isPresent()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message(String.format("Suggestion %s not found!",
                                id))
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }
            // check if the owner is the logged user
            if (user.getNationalId() != existingSuggestionOptional.get().getNationalId()) {
                throw new UnauthorisedException("You are not authorised to perform this action!");
            }

            // update the suggestion
            Suggestions eSuggestion = existingSuggestionOptional.get();
            if (dto.getCategory() != null) {
                eSuggestion.setCategory(dto.getCategory());
            }

            if (dto.getIgitekerezo() != null) {
                eSuggestion.setIgitekerezo(dto.getIgitekerezo());
            }

            if (dto.getLocation() != null && dto.getUrwego() == null) {

                eSuggestion.setLocation(dto.getLocation());
            }

            if (dto.getPhoneNumber() != null) {
                eSuggestion.setPhoneNumber(dto.getPhoneNumber());
            }

            if (dto.getUrwego() != null) {
                if (dto.getUrwego() == EUrwego.AKARERE || dto.getUrwego() == EUrwego.INTARA) {
                    // no prob about the upper level so
                    eSuggestion.setUpperLevel("none");
                }

                if (dto.getUpperLevel() == null) {
                    EUrwego missingUrwego = null;
                    switch (dto.getUrwego()) {
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
                            throw new BadRequestException(String.format("%s level is not found!", dto.getUrwego()));
                    }
                    throw new BadRequestException("Which " + missingUrwego + " is " + dto.getLocation() + " located!");
                }

                // update the upper level
                eSuggestion.setUrwego(dto.getUrwego());
                eSuggestion.setUpperLevel(dto.getUpperLevel());
            }

            Suggestions updatedSuggestion = suggestionRepository.save(eSuggestion);
            return ApiResponse.builder()
                    .data(updatedSuggestion)
                    .success(true)
                    .build();
        } catch (Exception e) {
            throw new Exception("Internal server error...");
        }
    }

    @Override
    public ApiResponse<Object> getAllMySuggestions() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            String nationalId = user.getNationalId();

            // the nationalid is like the owner of the suggestion
            List<Suggestions> suggestions = suggestionRepository.findAllByNationalId(nationalId);
            if (suggestions.isEmpty()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message(String.format("No suggestions found for user %s!",
                                nationalId))
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
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
                    leader.get().getOrganizationLevel(), leader.get().getLocation(), leader.get().getCategory());

            if (suggestions.isEmpty()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message(String.format("No suggestions found in %s and category: %s",
                                leader.get().getLocation(), leader.get().getCategory()))
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            List<Suggestions> filteredSuggestions = suggestions.stream().filter(sugg -> sugg.getStatus() == status)
                    .collect(Collectors.toList());

            if (filteredSuggestions.isEmpty()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("No " + status + " found!")
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
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
                    leader.get().getOrganizationLevel(), leader.get().getLocation(), leader.get().getCategory());

            if (suggestions.isEmpty()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message(String.format("No suggestions found in %s and category: %s",
                                leader.get().getLocation(), leader.get().getCategory()))
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
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

    @Override
    public ApiResponse<Object> deleteMySuggestion(Long id) throws Exception {
        try {
            // get the logged user
            UserResponse user = getLoggedUser.getLoggedUser();
            // get the suggestion
            Optional<Suggestions> suggestion = suggestionRepository.findById(id);
            if (!suggestion.isPresent()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("Suggestion " + id + " not found!").build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            // check the owner
            String ownerId = suggestion.get().getNationalId();

            if (user.getNationalId() == ownerId || user.getRole() == URole.ADMIN) {
                // delete the suggestion
                suggestionRepository.delete(suggestion.get());
                return ApiResponse.builder()
                        .data("Suggestion successfully deleted!")
                        .success(true)
                        .build();
            }
            throw new UnauthorisedException("You are not authorised to perform this action!");
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (UnauthorisedException e) {
            throw new UnauthorisedException(e.getMessage());
        } catch (Exception e) {
            throw new Exception("Internal server error...");
        }
    }

    @Override
    public ApiResponse<Object> getSuggestionById(Long id) throws Exception {
        try {
            // get the suggestion
            Optional<Suggestions> suggestion = suggestionRepository.findById(id);
            if (!suggestion.isPresent()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("Suggestion " + id + " not found!").build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            return ApiResponse.builder().data(suggestion).success(true).build();
        } catch (Exception e) {
            throw new Exception("Internal server error...");
        }
    }
}
