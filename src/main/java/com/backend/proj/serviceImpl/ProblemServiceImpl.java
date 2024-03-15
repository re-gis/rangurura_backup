package com.backend.proj.serviceImpl;

//import org.apache.coyote.BadRequestException;
import com.backend.proj.exceptions.BadRequestException;
import com.backend.proj.exceptions.NotFoundException;
import com.backend.proj.exceptions.UnauthorisedException;

import java.util.*;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.backend.proj.response.ApiResponse;
import com.backend.proj.response.NotFoundResponse;
import com.backend.proj.response.ProblemResponse;
import com.backend.proj.response.UserResponse;
import com.backend.proj.utils.GetLoggedUser;
import com.backend.proj.utils.UploadDoc;
import com.backend.proj.Services.ProblemService;
import com.backend.proj.dtos.CreateProblemDto;
import com.backend.proj.dtos.UpdateProblemDto;
import com.backend.proj.entities.Leaders;
import com.backend.proj.entities.Problem;
import com.backend.proj.entities.User;
import com.backend.proj.enums.ECategory;
import com.backend.proj.enums.EProblem_Status;
import com.backend.proj.enums.EUrwego;
import com.backend.proj.enums.URole;
import com.backend.proj.repositories.LeaderRepository;
import com.backend.proj.repositories.ProblemRepository;
import com.backend.proj.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {
    private final GetLoggedUser getLoggedUser;
    private final UploadDoc uploadDoc;
    private final ProblemRepository problemRepository;
    private final LeaderRepository leaderRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponse<Object> createAProblem(CreateProblemDto dto) throws Exception {
        try {
            // get logged in user
            // UserResponse user = getLoggedUser.getLoggedUser();

            if (dto.getCategory() == null || dto.getUrwego() == null || dto.getPhoneNumber() == null
                    || (dto.getIkibazo() == null && dto.getRecord() == null)) {
                throw new BadRequestException(
                        "Vuga ikibazo cyawe byibuze ushyireho urwego na kategori yacyo na \'proof\' ubundi wohereze!");
            }

            String recordUrl = "null";
            String ikibazo = "null";

            if (dto.getIkibazo() != null && dto.getRecord() != null) {
                ikibazo = dto.getIkibazo();
                recordUrl = uploadDoc.uploadRecord(dto.getRecord());
            } else if (dto.getIkibazo() != null) {
                ikibazo = dto.getIkibazo();
            } else if (dto.getRecord() != null) {
                recordUrl = uploadDoc.uploadRecord(dto.getRecord());
            } else {
                throw new BadRequestException("At least a record or text is required!");
            }

            String docUrl = null;
            if (dto.getProof() != null) {
                docUrl = uploadDoc.uploadDoc(dto.getProof());
            }

            // create the object
            Problem problem = Problem.builder()
                    .category(dto.getCategory())
                    .ikibazo(ikibazo)
                    .phoneNumber(dto.getPhoneNumber())
                    .proofUrl(docUrl)
                    .recordUrl(recordUrl)
                    .status(EProblem_Status.PENDING)
                    .owner(dto.getPhoneNumber())
                    .urwego(dto.getUrwego())
                    .build();

            problemRepository.save(problem);

            ProblemResponse response = new ProblemResponse();
            response.setMessage(
                    "Ikibazo cyawe cyoherejwe kubashinzwe kugikurikirana Tegereza igihe gito uraza gusubizwa!");
            response.setProblem(problem);
            return ApiResponse.builder()
                    .success(true)
                    .data(response)
                    .build();
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            System.out.println(e);
            throw new Exception("Internal server error...");
        }
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
                        .build();
                return ApiResponse.builder()
                        .data(response)
                        .success(true)
                        .build();
            }

            return ApiResponse.builder()
                    .data(problems)
                    .success(true)
                    .build();
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new Exception("Internal server error...");
        }
    }

    @Override
    public ApiResponse<Object> deleteQuestion(Long id) throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            // find the problem of the logged user to be deleted
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
            throw new Exception("Internal server error...");
        }
    }

    @Override
    public ApiResponse<Object> updateMyProblem(UpdateProblemDto dto, Long id) throws Exception {
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
            throw new Exception("Internal server error...");
        }
    }

    @PreAuthorize("hasRole('UMUYOBOZI')")
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

            // get all the problems and filter them
            List<Problem> problems = problemRepository.findAllByUrwegoAndCategory(leader.get().getOrganizationLevel(),
                    leader.get().getCategory());
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
            EUrwego urwego = leader.get().getOrganizationLevel();
            for (Problem problem : problems) {
                String owner = problem.getOwner();
                // get the same user
                Optional<User> userResponse = userRepository.findByNationalId(owner);
                if (!userResponse.isPresent()) {
                    NotFoundResponse response = NotFoundResponse.builder()
                            .message(
                                    "Owner " + owner
                                            + " not found!")
                            .build();
                    return ApiResponse.builder()
                            .data(response)
                            .success(true)
                            .build();
                }

                // get the user's location same to that of the leader
                switch (urwego) {
                    case AKAGARI:
                        // check the user with the same akagari
                        if (userResponse.get().getCell() == leader.get().getLocation()) {
                            filteredProblems.add(problem);
                        }
                        break;
                    case INTARA:
                        if (userResponse.get().getProvince() == leader.get().getLocation()) {
                            filteredProblems.add(problem);
                        }
                        break;

                    case AKARERE:
                        if (userResponse.get().getProvince() == leader.get().getLocation()) {
                            filteredProblems.add(problem);
                        }
                        break;

                    case UMUDUGUDU:
                        if (userResponse.get().getProvince() == leader.get().getLocation()) {
                            filteredProblems.add(problem);
                        }
                        break;

                    case UMURENGE:
                        if (userResponse.get().getProvince() == leader.get().getLocation()) {
                            filteredProblems.add(problem);
                        }
                        break;
                    default:
                        NotFoundResponse response = NotFoundResponse.builder()
                                .message(
                                        "No problems found in your location!")
                                .build();
                        return ApiResponse.builder()
                                .data(response)
                                .success(true)
                                .build();

                }
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

            return ApiResponse.builder()
                    .data(filteredProblems)
                    .success(true)
                    .build();

        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (UnauthorisedException e) {
            throw new UnauthorisedException(e.getMessage());
        } catch (Exception e) {
            throw new Exception("Internal server error...");
        }
    }

    @Override
    public ApiResponse<Object> getProblemById(Long id) throws Exception {
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
            throw new Exception("Internal server error...");
        }
    }

    @PreAuthorize("hasRole('UMUYOBOZI')")
    @Override
    public ApiResponse<Object> getProblemsByStatus(EProblem_Status status) throws Exception {
        try {
            List<Problem> problems = (List<Problem>) getMyLocalProblems().getData();

            // filter according to the status provided
            List<Problem> filteredProblems = new ArrayList<>();
            for (Problem problem : problems) {
                if (problem.getStatus() == status) {
                    filteredProblems.add(problem);
                }
            }

            return ApiResponse.builder()
                    .data(filteredProblems)
                    .success(true)
                    .build();
        } catch (Exception e) {
            throw new Exception("Internal server error...");
        }
    }

}
