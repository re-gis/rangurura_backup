package com.backend.proj.serviceImpl;

import com.backend.proj.Services.ProblemService;
import com.backend.proj.Services.SuggestionService;
import com.backend.proj.dtos.SuggestionDto;
import com.backend.proj.dtos.SuggestionUpdateDto;
import com.backend.proj.entities.Leaders;
import com.backend.proj.entities.Suggestions;
import com.backend.proj.enums.*;
import com.backend.proj.exceptions.BadRequestException;
import com.backend.proj.exceptions.InvalidEnumConstantException;
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
import com.backend.proj.utils.ValidateEnum;

import java.util.*;

import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.cloudinary.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
public class SuggestionServiceImpl implements SuggestionService {
    private final SuggestionRepository suggestionRepository;
    private final GetLoggedUser getLoggedUser;
    private final LeaderRepository leaderRepository;
    private final ValidateEnum validateEnum;
    private static final Logger logger = LoggerFactory.getLogger(ProblemService.class);
    private static final String PYTHON_API_URL = "https://rangurura-ai.up.railway.app/check_similar_suggestions";
    private RestTemplate restTemplate;

    // Setter for restTemplate
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

@Override
public ApiResponse<Object> PostSuggestion(SuggestionDto dto) throws Exception {
    try {
        // Send suggestion data to Python API to check for similar suggestions
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(PYTHON_API_URL , dto, String.class);
        String response = responseEntity.getBody();

        // Check if response is null or empty
        if (response == null || response.isEmpty()) {
            throw new RuntimeException("Empty response received from AI");
        }

        // Parse response from Python API
        JSONObject jsonResponse = new JSONObject(response);

        // Check if the response contains error
        if (jsonResponse.has("error")) {
            String errorMessage = jsonResponse.getString("error");
            throw new RuntimeException("Internal server error from AI " + errorMessage);
        }
        boolean similarSuggestionExists = jsonResponse.optBoolean("similar_suggestion_exists", false);
        String similarSuggestionDescription = jsonResponse.optString("similar_suggestions", "");
        String message = "The similar suggestion has been reported by another person!";

        if (similarSuggestionExists) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("message", message);
            responseData.put("similarSuggestionDescription", similarSuggestionDescription);

            return ApiResponse.builder()
                    .data(responseData)
                    .success(false)  // Set success to false when similar problem exists
                    .build();
        }
        else {
            // If no similar suggestion exists, proceed to save the suggestion to the repository
            Suggestions suggestionEntity = convertDtoToEntity(dto);
            Suggestions savedSuggestion = suggestionRepository.save(suggestionEntity);
            if (savedSuggestion != null) {
                SuggestionResponse response1 = new SuggestionResponse();
                response1.setMessage("Suggestion sent successfully");
                response1.setSuggestion(savedSuggestion);
                return ApiResponse.builder()
                        .data(response1)
                        .success(true)
                        .build();
            } else {
                throw new ServiceException("Suggestion not sent ,  Please try again later !");
            }
        }
    } catch (InvalidEnumConstantException e) {
        throw new BadRequestException(e.getMessage());
    } catch (ServiceException | BadRequestException e) {
        throw new ServiceException(e.getMessage());
    } catch (Exception e) {
        throw new Exception(e.getMessage());
    }
}



    @Override
    public ApiResponse<Object> UpdateSuggestion(SuggestionUpdateDto dto, UUID id) throws Exception {
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

            EUrwego missingUrwego = null;
            if (dto.getUrwego() != null) {
                if (dto.getUrwego() == EUrwego.AKARERE || dto.getUrwego() == EUrwego.INTARA) {
                    // no prob about the upper level so
                    eSuggestion.setUpperLevel(EUrwego.INTARA);
                }

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
            }

            // update the upper level
            eSuggestion.setUrwego(dto.getUrwego());
            eSuggestion.setUpperLevel(missingUrwego);

            Suggestions updatedSuggestion = suggestionRepository.save(eSuggestion);
            return ApiResponse.builder()
                    .data(updatedSuggestion)
                    .success(true)
                    .build();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
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
            throw new Exception(e.getMessage());
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
            System.out.println(user.getRole());
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

            for (Suggestions suggestion : suggestions) {
                System.out.println(suggestion.getId());
            }

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
            System.out.println();
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
            suggestions.setUpperLevel(EUrwego.INTARA);
        } else {
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
            suggestions.setUpperLevel(missingUrwego);
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
    public ApiResponse<Object> deleteMySuggestion(UUID id) throws Exception {
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

            if (user.getNationalId().equals(ownerId) || user.getRole() == URole.ADMIN) {
                // delete the suggestion
                suggestionRepository.delete(suggestion.get());
                return ApiResponse.builder()
                        .data("Suggestion successfully deleted!")
                        .success(true)
                        .build();
            }

//            System.out.println("the nationalId of suggestions is " + ownerId);
//            System.out.println("The nationalId of user is "+ user.getNationalId());
            throw new UnauthorisedException("You are not authorised to perform this action!");
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (UnauthorisedException e) {
            throw new UnauthorisedException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


    @Override
    public ApiResponse<Object> getSuggestionById(UUID id) throws Exception {
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
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ApiResponse<Object> getAllSuggestions() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();

            if (user.getRole() != URole.ADMIN) {
                throw new UnauthorisedException("You are not allowed to perform this action!");
            }

            // fetch the suggestions
            List<Suggestions> suggestions = suggestionRepository.findAll();
            if (suggestions.isEmpty()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("No suggestions found!")
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .status(HttpStatus.OK)
                        .build();
            }

            return ApiResponse.builder()
                    .data(suggestions)
                    .success(true)
                    .status(HttpStatus.OK)
                    .build();

        } catch (Exception e) {
            System.out.println(e);
            throw new Exception(e.getMessage());
        }
    }

    // To get the number of all suggestions by admin
    @Override
    public ApiResponse<Object> getNumberOfAllSuggestions() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            if (user != null && user.getRole() == URole.ADMIN) {
                long numberOfSuggestions = suggestionRepository.count();
                logger.info("Number of suggestions retrieved successfully: {}", numberOfSuggestions);
                return ApiResponse.builder()
                        .data(numberOfSuggestions)
                        .success(true)
                        .build();
            } else {
                if (user == null) {
                    logger.warn("User is not logged in");
                } else {
                    logger.warn("User {} does not have ADMIN role", user.getName());
                }
                return ApiResponse.builder()
                        .data("You are not authorized to perform this action")
                        .success(false)
                        .build();
            }
        } catch (Exception e) {
            logger.error("Error in fetching suggestions", e); // Include the exception in the log
            return ApiResponse.builder()
                    .data("Error in fetching suggestions")
                    .success(false)
                    .build();
        }
    }



    //get the number of my suggestion
    @Override
    public ApiResponse<Object> getNumberOfAcceptedSuggestionForMe() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            if (user != null) {
                long numberOfAcceptedSuggestions = suggestionRepository.countByStatusAndNationalId(ESuggestion.ACCEPTED, user.getNationalId());
                return ApiResponse.builder()
                        .data(numberOfAcceptedSuggestions)
                        .success(true)
                        .build();
            } else {
                return ApiResponse.builder()
                        .data("Login to continue")
                        .success(false)
                        .build();
            }

        } catch (Exception e) {
            logger.error("Error occurred while getting the number of my accepted suggestions", e);
            return ApiResponse.builder()
                    .data("An error occurred while fetching the number of accepted suggestions.")
                    .success(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Object> getNumberOfAllOnMyLocal() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            if (user.getRole() != URole.UMUYOBOZI) {
                throw new UnauthorisedException("You are not authorised to perform this action!");
            }

            Optional<Leaders> leader = leaderRepository.findByNationalId(user.getNationalId());
            if (!leader.isPresent()) {
                throw new NotFoundException("Leader " + user.getNationalId() + " not found!");
            }

            // Count the suggestions
            long numberOfSuggestions = suggestionRepository.countAllByUrwegoAndLocationAndCategory(
                    leader.get().getOrganizationLevel(), leader.get().getLocation(), leader.get().getCategory());


            return ApiResponse.builder()
                    .data(numberOfSuggestions)
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




}
