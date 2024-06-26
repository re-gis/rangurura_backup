package com.backend.proj.serviceImpl;

import com.backend.proj.exceptions.BadRequestException;
import com.backend.proj.exceptions.InvalidEnumConstantException;
import com.backend.proj.exceptions.NotFoundException;
import com.backend.proj.exceptions.UnauthorisedException;

import java.io.IOException;
import java.util.*;

import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.backend.proj.response.ApiResponse;
import com.backend.proj.response.NotFoundResponse;
import com.backend.proj.response.ProblemResponse;
import com.backend.proj.response.UserResponse;
import com.backend.proj.utils.GetLoggedUser;
import com.backend.proj.utils.UploadDoc;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.backend.proj.Services.ProblemService;
import com.backend.proj.dtos.CreateProblemDto;
import com.backend.proj.dtos.EscalateProblemDto;
import com.backend.proj.dtos.SolveProblemDto;
import com.backend.proj.dtos.UpdateProblemDto;
import com.backend.proj.entities.Leaders;
import com.backend.proj.entities.Problem;
import com.backend.proj.enums.ECategory;
import com.backend.proj.enums.EProblem_Status;
import com.backend.proj.enums.EUrwego;
import com.backend.proj.enums.URole;
import com.backend.proj.repositories.LeaderRepository;
import com.backend.proj.repositories.ProblemRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {
    private final GetLoggedUser getLoggedUser;
    private final UploadDoc uploadDoc;
    private final ProblemRepository problemRepository;
    private final LeaderRepository leaderRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProblemService.class);
    private static final String PYTHON_API_URL = "https://rangurura-ai.up.railway.app/check_similar_problem";
    private RestTemplate restTemplate;
    private final OtpServiceImpl otpServiceImpl;

    // private RestTemplate restTemplate; // Inject RestTemplate bean here

    // Setter for restTemplate
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ApiResponse<Object> createAProblem(CreateProblemDto dto) throws Exception {
        try {
            // Send problem data to Python API
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(PYTHON_API_URL, dto, String.class);
            String response = responseEntity.getBody();

            // Check if response is null or empty
            if (response == null || response.isEmpty()) {
                throw new RuntimeException("Empty response received from Python API");
            }

            // Parse response from Python API
            JSONObject jsonResponse = new JSONObject(response);

            // Check if the response contains error
            if (jsonResponse.has("error")) {
                String errorMessage = jsonResponse.getString("error");
                throw new RuntimeException("Internal server error from AI  " + errorMessage);
            }

            boolean similarProblemExists = jsonResponse.optBoolean("similar_problem_exists", false);
            String similarProblemDescription = jsonResponse.optString("similar_problem", "");
            String message = "The similar problem has been reported by another person!";

            if (similarProblemExists) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("message", message);
                responseData.put("similarProblemDescription", similarProblemDescription);

                return ApiResponse.builder()
                        .data(responseData)
                        .success(false) // Set success to false when similar problem exists
                        .build();
            } else {
                return createNewProblem(dto);
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Communication error with AI ", e);
        }
    }

    private ApiResponse<Object> createNewProblem(CreateProblemDto dto) throws Exception {
        // Create the problem object
        Problem problem = buildProblem(dto);

        // Save the problem to the database
        problemRepository.save(problem);

        // Create the response
        ProblemResponse response = new ProblemResponse();
        response.setMessage("Ikibazo cyawe cyoherejwe kubashinzwe kugikurikirana Tegereza igihe gito uraza gusubizwa!");
        response.setProblem(problem);

        return ApiResponse.builder()
                .success(true)
                .data(response)
                .build();
    }

    private Problem buildProblem(CreateProblemDto dto) throws IOException {
        // Build the problem object
        String recordUrl = (dto.getRecord() != null) ? uploadDoc.uploadRecord(dto.getRecord()) : "null";
        String docUrl = (dto.getProof() != null) ? uploadDoc.uploadDoc(dto.getProof()) : "null";
        return Problem.builder()
                .category(dto.getCategory())
                .ikibazo(dto.getIkibazo())
                .phoneNumber(dto.getPhoneNumber())
                .proofUrl(docUrl)
                .recordUrl(recordUrl)
                .status(EProblem_Status.PENDING)
                .owner(dto.getNationalId())
                .urwego(dto.getUrwego())
                .target(dto.getTarget())
                .build();
    }

    @Override
    public ApiResponse<Object> getMyAskedProblems() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            // get problems I own
            Problem[] problems = problemRepository.findAllByOwner(user.getNationalId());
            if (problems.length == 0) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("No problems found for user: " + user
                                .getName())
                        .data(problems)
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }
            Arrays.sort(problems, Comparator.comparing(Problem::getCreatedAt).reversed());

            return ApiResponse.builder()
                    .data(problems)
                    .success(true)
                    .build();
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ApiResponse<Object> deleteQuestion(UUID id) throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            // find the problem of the logged user to be deleted
            Problem[] problems = problemRepository.findAllByOwner(user.getNationalId());
            if (problems.length == 0) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("No problems found for user: " + user
                                .getName())
                        .data(problems)
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            if (id == null) {
                throw new BadRequestException("Problem id is required!");
            }

            // get the problem of the same id
            Optional<Problem> problemToDelete = Arrays.stream(problems)
                    .filter(problem -> problem.getId().equals(id))
                    .findFirst();

            if (problemToDelete.isEmpty()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("Problem " + id
                                + " not found!")
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            problemRepository.delete(problemToDelete.get());

            return ApiResponse.builder().data("Problem " + id + " deleted successfully!").build();
        } catch (BadRequestException e) {
            throw new BadRequestException("Problem id is required!");
        } catch (NotFoundException e) {
            throw new NotFoundException("No problems found!");
        } catch (Exception e) {
            System.out.println(e);
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ApiResponse<Object> updateMyProblem(UpdateProblemDto dto, UUID id) throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            if (id == null) {
                throw new BadRequestException("Problem id is required!");
            }

            // get the problem by user and id
            Problem[] problems = problemRepository.findAllByOwner(user.getNationalId());
            if (problems.length == 0) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("No problems found for user: " + user
                                .getName())
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }
            Optional<Problem> problemToUpdate = Arrays.stream(problems)
                    .filter(problem -> problem.getId().equals(id))
                    .findFirst();

            if (problemToUpdate.isEmpty()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("Problem " + id
                                + " not found!")
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            Problem problem = problemToUpdate.get();
            Optional<ECategory> cat = dto.getCategory();
            ECategory category = cat.orElse(problem.getCategory());
            Optional<String> iki = dto.getIkibazo();
            String ikibazo = iki.orElse(problem.getIkibazo());
            Optional<EUrwego> urwe = dto.getUrwego();
            EUrwego urwego = urwe.orElse(problem.getUrwego());
            Optional<String> num = dto.getNumber();
            String number = num.orElse(problem.getPhoneNumber());

            String proof = problem.getProofUrl();
            String record = problem.getRecordUrl();

            if (dto.getProof() != null) {
                proof = uploadDoc.uploadDoc(dto.getProof());
            }

            if (dto.getRecord() != null) {
                record = uploadDoc.uploadRecord(dto.getRecord());
            }

            problem.setCategory(category);
            problem.setIkibazo(ikibazo);
            problem.setOwner(user.getNationalId());
            problem.setPhoneNumber(number);
            problem.setProofUrl(proof);
            problem.setRecordUrl(record);
            problem.setUrwego(urwego);

            problemRepository.save(problem);

            return ApiResponse.builder()
                    .data(problem)
                    .success(true)
                    .build();

        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    // @PreAuthorize("hasRole('UMUYOBOZI')")
    @Override
    public ApiResponse<Object> getMyLocalProblems() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            // get the leader
            if (user.getRole() != URole.UMUYOBOZI) {
                throw new UnauthorisedException("You are not authorised to perform this action!");
            }

            // get the leader
            Optional<Leaders> leader = leaderRepository.findByNationalId(user.getNationalId());
            if (!leader.isPresent()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message(
                                "Leader not found!")
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            /**
             * here after getting the leader I have to get the problems whose category ==
             * leader.category || leader.category == "HEAD"
             * 
             * also the problems' urwego = leader.organizationLevel
             * 
             * also the problems' target = leader.location
             */

            // get all the problems and filter them
            List<Problem> problems = problemRepository.findAllByUrwegoAndCategoryAndTarget(
                    leader.get().getOrganizationLevel(),
                    leader.get().getCategory(), leader.get().getLocation());
            if (problems.isEmpty()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message(
                                "No problems found!")
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            List<Problem> filteredProblems = new ArrayList<>();
            // EUrwego urwego = leader.get().getOrganizationLevel();
            for (Problem problem : problems) {
                filteredProblems.add(problem);
            }

            if (filteredProblems.isEmpty()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message(
                                "No problems found!")
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            filteredProblems.sort(Comparator.comparing(Problem::getCreatedAt).reversed());

            return ApiResponse.builder()
                    .data(filteredProblems)
                    .success(true)
                    .build();

        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (UnauthorisedException e) {
            throw new UnauthorisedException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ApiResponse<Object> getProblemById(UUID id) throws Exception {
        try {
            Optional<Problem> problem = problemRepository.findById(id);
            if (!problem.isPresent()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message(
                                "Problem " + id
                                        + " not found!")
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            return ApiResponse.builder()
                    .data(problem)
                    .success(true)
                    .build();

        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    // @PreAuthorize("hasRole('UMUYOBOZI')")
    @Override
    public ApiResponse<Object> getProblemsByStatus(EProblem_Status status) throws Exception {
        try {
            @SuppressWarnings("unchecked")
            List<Problem> problems = (List<Problem>) getMyLocalProblems().getData();

            // filter according to the status provided
            List<Problem> filteredProblems = new ArrayList<>();
            for (Problem problem : problems) {
                if (problem.getStatus() == status) {
                    filteredProblems.add(problem);
                }
            }

            filteredProblems.sort(Comparator.comparing(Problem::getCreatedAt).reversed());

            return ApiResponse.builder()
                    .data(filteredProblems)
                    .success(true)
                    .build();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    // this is to get the number of all probs by admin
    @Override
    public ApiResponse<Object> getNumberOfAllProb() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            if (user != null && user.getRole() == URole.ADMIN) {
                long numberOfProblems = problemRepository.count();
                return ApiResponse.builder()
                        .data(numberOfProblems)
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
            logger.error("Error in fetching problems", e); // Include the exception in the log
            return ApiResponse.builder()
                    .data("Error in fetching problems")
                    .success(false)
                    .build();
        }
    }

    // get number of all pending probs
    @Override
    public ApiResponse<Object> getNumberOfPendingProblems() throws Exception {

        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            if (user != null && user.getRole() == URole.ADMIN) {
                long numberOfCheckedProblems = problemRepository.countByStatus(EProblem_Status.PENDING);
                return ApiResponse.builder()
                        .data(numberOfCheckedProblems)
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
            logger.error("Error in fetching problems", e); // Include the exception in the log
            return ApiResponse.builder()
                    .data("Error in fetching problems")
                    .success(false)
                    .build();
        }
    }

    // get number of all approved probs
    @Override
    public ApiResponse<Object> getNumberOfApprovedProblems() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            if (user != null && user.getRole() == URole.ADMIN) {
                long numberOfApprovedProblems = problemRepository.countByStatus(EProblem_Status.APPROVED);
                return ApiResponse.builder()
                        .data(numberOfApprovedProblems)
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
            logger.error("Error in fetching problems", e); // Include the exception in the log
            return ApiResponse.builder()
                    .data("Error in fetching problems")
                    .success(false)
                    .build();
        }
    }

    // get rejected probs
    // @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ApiResponse<Object> getNumberOfRejectedProblems() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            if (user != null && user.getRole() == URole.ADMIN) {
                long numberOfRejectedProblems = problemRepository.countByStatus(EProblem_Status.REJECTED);
                return ApiResponse.builder()
                        .data(numberOfRejectedProblems)
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
            logger.error("Error in fetching problems", e); // Include the exception in the log
            return ApiResponse.builder()
                    .data("Error in fetching problems")
                    .success(false)
                    .build();
        }
    }

    // get number of solved probs for user
    @Override
    public ApiResponse<Object> getNumberOfSolvedProblemsForUser() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            if (user != null) {
                long numberOfSolvedProblems = problemRepository.countByStatusAndOwner(EProblem_Status.APPROVED,
                        user.getNationalId());
                return ApiResponse.builder()
                        .data(numberOfSolvedProblems)
                        .success(true)
                        .build();
            } else {
                return ApiResponse.builder()
                        .data("Login to continue")
                        .success(false)
                        .build();
            }

        } catch (Exception e) {
            logger.error("Error occurred while getting the number of solved problems for the user", e);
            return ApiResponse.builder()
                    .data("An error occurred while fetching the number of solved problems.")
                    .success(false)
                    .build();
        }
    }

    // get number of pending prob for user
    @Override
    public ApiResponse<Object> getNumberOfPendingProblemsForUser() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            if (user != null) {
                long numberOfUnSolvedProblems = problemRepository.countByStatusAndOwner(EProblem_Status.PENDING,
                        user.getNationalId());
                return ApiResponse.builder()
                        .data(numberOfUnSolvedProblems)
                        .success(true)
                        .build();
            } else {
                return ApiResponse.builder()
                        .data("Login to continue")
                        .success(false)
                        .build();
            }

        } catch (Exception e) {
            logger.error("Error occurred while getting the number of solved problems for the user", e);
            return ApiResponse.builder()
                    .data("An error occurred while fetching the number of solved problems.")
                    .success(false)
                    .build();
        }
    }

    // get number of probs on his level

    @Override
    public ApiResponse<Object> getNumberOfProOnMyLevel() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();

            // Check if the user is authorized
            if (user.getRole() != URole.UMUYOBOZI) {
                throw new UnauthorisedException("You are not authorized to perform this action!");
            }

            // Find the leader
            Optional<Leaders> leader = leaderRepository.findByNationalId(user.getNationalId());
            if (!leader.isPresent()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("Leader not found!")
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            System.out.println(leader.get().getNationalId());

            // Count the number of problems to be solved
            long numberOfProblems = problemRepository.countAllByUrwegoAndCategoryAndTarget(
                    leader.get().getOrganizationLevel(),
                    leader.get().getCategory(), leader.get().getLocation());

            System.out.println(numberOfProblems);

            return ApiResponse.builder()
                    .data(numberOfProblems)
                    .success(true)
                    .build();

        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (UnauthorisedException e) {
            throw new UnauthorisedException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ApiResponse<Object> getNumberOfPendingProbsOnMyLevel() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();

            // Check if the user is authorized
            if (user.getRole() != URole.UMUYOBOZI) {
                throw new UnauthorisedException("You are not authorized to perform this action!");
            }

            // Find the leader
            Optional<Leaders> leader = leaderRepository.findByNationalId(user.getNationalId());
            if (!leader.isPresent()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("Leader not found!")
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            // Count the number of problems to be solved
            long numberOfProblems = problemRepository.countAllByUrwegoAndCategoryAndTargetAndStatus(
                    leader.get().getOrganizationLevel(),
                    leader.get().getCategory(), leader.get().getLocation(), EProblem_Status.PENDING);

            return ApiResponse.builder()
                    .data(numberOfProblems)
                    .success(true)
                    .build();

        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (UnauthorisedException e) {
            throw new UnauthorisedException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ApiResponse<Object> getNumberOfApprovedProbsOnMyLevel() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();

            // Check if the user is authorized
            if (user.getRole() != URole.UMUYOBOZI) {
                throw new UnauthorisedException("You are not authorized to perform this action!");
            }

            // Find the leader
            Optional<Leaders> leader = leaderRepository.findByNationalId(user.getNationalId());
            if (!leader.isPresent()) {
                NotFoundResponse response = NotFoundResponse.builder()
                        .message("Leader not found!")
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            // Count the number of problems to be solved
            long numberOfProblems = problemRepository.countAllByUrwegoAndCategoryAndTargetAndStatus(
                    leader.get().getOrganizationLevel(),
                    leader.get().getCategory(), leader.get().getLocation(), EProblem_Status.APPROVED);

            return ApiResponse.builder()
                    .data(numberOfProblems)
                    .success(true)
                    .build();

        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (UnauthorisedException e) {
            throw new UnauthorisedException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ApiResponse<Object> escalateManually(EscalateProblemDto dto) throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            if (user.getRole() != URole.UMUYOBOZI) {
                throw new UnauthorisedException("You are not allowed to perform this action!");
            }
            UUID problemId = dto.getProblemId();
            Optional<Problem> problem = problemRepository.findById(problemId);
            if (!problem.isPresent()) {
                // return
                // ApiResponse.builder().data(problem).success(true).status(HttpStatus.OK).build();
                throw new NotFoundException("Problem " + problemId + " does not exist!");
            } else {
                EUrwego nextLevel;
                // here we switch the levels
                switch (problem.get().getUrwego()) {
                    case UMUDUGUDU:
                        nextLevel = EUrwego.AKAGARI;
                        break;
                    case AKAGARI:
                        nextLevel = EUrwego.UMURENGE;
                        break;
                    case UMURENGE:
                        nextLevel = EUrwego.AKARERE;
                        break;
                    case AKARERE:
                        nextLevel = EUrwego.INTARA;
                        break;
                    default:
                        throw new InvalidEnumConstantException("Enum not found!");
                }
                String target = dto.getTarget();
                if (target == null || target.isBlank()) {
                    throw new BadRequestException("Please provide the name of that " + nextLevel);
                } else {
                    // problem.get().setCreatedAt(LocalDateTime.now());
                    problem.get().setStatus(EProblem_Status.ESCALATED);
                    problem.get().setUrwego(nextLevel);
                    problem.get().setTarget(target);
                    problemRepository.save(problem.get());

                    return ApiResponse.builder()
                            .data("Problem " + problem.get().getId() + " has been escalated successfully").success(true)
                            .status(HttpStatus.OK).build();
                }

            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ApiResponse<Object> getAllProblems() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            if (user.getRole() != URole.ADMIN) {
                throw new UnauthorisedException("You are not authorised to perform this action!");
            }

            List<Problem> problems = problemRepository.findAll();
            if (!problems.isEmpty()) {
                problems.sort(Comparator.comparing(Problem::getCreatedAt).reversed());
            }

            return ApiResponse.builder()
                    .data(problems)
                    .success(true)
                    .status(HttpStatus.OK)
                    .build();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ApiResponse<Object> solveProblem(UUID problemId, SolveProblemDto dto) throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            if (user.getRole() != URole.UMUYOBOZI) {
                throw new UnauthorisedException("You are not authorised to perform this action!");
            } else {
                Optional<Problem> problem = problemRepository.findById(problemId);
                if (!problem.isPresent() || problem.isEmpty()) {
                    return ApiResponse.builder().data(problem.get()).success(true).status(HttpStatus.OK)
                            .message("Problem " + problemId + " not found!").build();
                } else {
                    // get the message to send to problem sender
                    String message = dto.getMessage();
                    String phoneNumber = problem.get().getPhoneNumber();
                    if (message == null || message.isBlank() || message.isEmpty()) {
                        throw new BadRequestException("Please provide the message to send to the user...");
                    } else {
                        // send the message
                        otpServiceImpl.sendMessage(phoneNumber, message);
                        return ApiResponse.builder().data("Response sent to " + phoneNumber + " successfully...")
                                .status(HttpStatus.OK).success(true).build();
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

}
