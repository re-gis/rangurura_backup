package com.backend.rangurura.serviceImpl;

//import org.apache.coyote.BadRequestException;
import com.backend.rangurura.exceptions.BadRequestException;
import com.backend.rangurura.exceptions.NotFoundException;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.backend.rangurura.response.ApiResponse;
import com.backend.rangurura.response.ProblemResponse;
import com.backend.rangurura.response.UserResponse;
import com.backend.rangurura.utils.GetLoggedUser;
import com.backend.rangurura.utils.UploadDoc;
import com.backend.rangurura.Services.ProblemService;
import com.backend.rangurura.dtos.CreateProblemDto;
import com.backend.rangurura.dtos.UpdateProblemDto;
import com.backend.rangurura.entities.Problem;
import com.backend.rangurura.enums.ECategory;
import com.backend.rangurura.enums.EProblem_Status;
import com.backend.rangurura.enums.EUrwego;
import com.backend.rangurura.repositories.ProblemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {
    private final GetLoggedUser getLoggedUser;
    private final UploadDoc uploadDoc;
    private final ProblemRepository problemRepository;

    @Override
    public ApiResponse<Object> createAProblem(CreateProblemDto dto) throws Exception {
        try {
            // get logged in user
            UserResponse user = getLoggedUser.getLoggedUser();
            System.out.println(user);

            if (dto.getCategory() == null || dto.getUrwego() == null || dto.getPhoneNumber() == null
                    || dto.getProof() == null
                    || (dto.getIkibazo() == null && dto.getRecord() == null)) {
                throw new BadRequestException(
                        "Vuga ikibazo cyawe byibuze ushyireho urwego na kategori yacyo na \'proof\' ubundi wohereze!");
            }

            String recordUrl = "null";
            String ikibazo = "null";

            if (dto.getIkibazo() != null) {
                ikibazo = dto.getIkibazo();
            } else if (dto.getRecord() != null) {
                recordUrl = uploadDoc.uploadRecord(dto.getRecord());
            } else if (dto.getIkibazo() != null && dto.getRecord() != null) {
                ikibazo = dto.getIkibazo();
                recordUrl = uploadDoc.uploadRecord(dto.getRecord());
            } else {
                throw new BadRequestException("At least a record or text is required!");
            }

            System.out.println(recordUrl);
            System.out.println(dto.getRecord());

            String docUrl = uploadDoc.uploadDoc(dto.getProof());

            // create the object
            Problem problem = Problem.builder()
                    .category(dto.getCategory())
                    .ikibazo(ikibazo)
                    .phoneNumber(dto.getPhoneNumber())
                    .proofUrl(docUrl)
                    .recordUrl(recordUrl)
                    .status(EProblem_Status.PENDING)
                    .owner(user.getNationalId())
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
    public Problem[] getMyAskedProblems() throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            // get problems I own
            Problem[] problems = problemRepository.findAllByOwner(user.getNationalId());
            if (problems.length == 0) {
                throw new NotFoundException("No problems found for user: " + user.getName());
            }

            return problems;
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new Exception("Internal server error...");
        }
    }

    @Override
    public String deleteQuestion(Long id) throws Exception {
        try {
            UserResponse user = getLoggedUser.getLoggedUser();
            // find the problem of the logged user to be deleted
            Problem[] problems = problemRepository.findAllByOwner(user.getNationalId());
            if (problems.length == 0) {
                throw new NotFoundException("No problems found for user: " + user.getName());
            }

            if (id == null) {
                throw new BadRequestException("Problem id is required!");
            }

            // get the problem of the same id
            Optional<Problem> problemToDelete = Arrays.stream(problems)
                    .filter(problem -> problem.getId().equals(id))
                    .findFirst();

            if (problemToDelete.isEmpty()) {
                throw new NotFoundException("Problem " + id + " not found!");
            }

            problemRepository.delete(problemToDelete.get());

            return "Problem " + id + " deleted successfully!";
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
                throw new NotFoundException("No problems found for user: " + user.getName());
            }
            Optional<Problem> problemToUpdate = Arrays.stream(problems)
                    .filter(problem -> problem.getId().equals(id))
                    .findFirst();

            if (problemToUpdate.isEmpty()) {
                throw new NotFoundException("Problem " + id + " not found!");
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

}
