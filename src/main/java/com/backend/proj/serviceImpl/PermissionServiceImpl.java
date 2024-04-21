package com.backend.proj.serviceImpl;

import com.backend.proj.Services.PermissionService;
import com.backend.proj.dtos.GrantPermissionToLeaderDto;
import com.backend.proj.entities.Permission;
//import com.backend.proj.entities.User;
import com.backend.proj.enums.EUrwego;
import com.backend.proj.enums.URole;
import com.backend.proj.exceptions.BadRequestException;
import com.backend.proj.exceptions.UnauthorisedException;
import com.backend.proj.repositories.PermissionRepository;
import com.backend.proj.repositories.UserRepository;
import com.backend.proj.response.ApiResponse;
import com.backend.proj.response.UserResponse;
import com.backend.proj.utils.GetLoggedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PermissionServiceImpl implements PermissionService {
    private final GetLoggedUser getLoggedUser;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponse<Object> registerNewPermission(GrantPermissionToLeaderDto dto) throws Exception {
        try {
            UserResponse loggedUser = getLoggedUser.getLoggedUser();

            // Check if the logged user is a district leader
            if (loggedUser.getRole() != URole.UMUYOBOZI) {
                throw new UnauthorisedException("You are not allowed to perform this action!");
            }

            // Check if the logged user's account is verified
            if (!loggedUser.isVerified()) {
                throw new UnauthorisedException("Verify the account to continue please!");
            }

            // Fetch the permission record of the logged user
            Optional<Permission> permissionOpt = permissionRepository.findByNationalId(loggedUser.getNationalId());
            Permission loggedUserPermission = permissionOpt.orElseThrow(() -> new Exception("Permission record not found for the logged user."));

            // Handle permission assignment based on the organization level of the logged user
            switch (loggedUserPermission.getOrganizationLevel()) {
                case INTARA:
                    handlePermissionAssignment(dto, EUrwego.AKARERE);
                    break;
                case AKARERE:
                    handlePermissionAssignment(dto, EUrwego.UMURENGE);
                    break;
                case UMURENGE:
                    handlePermissionAssignment(dto, EUrwego.AKAGARI);
                    break;
                default:
                    throw new UnsupportedOperationException("Organization level not supported yet!");
            }

            return ApiResponse.builder()
                    .data("Leader successfully registered, verify to continue to the system...")
                    .success(true)
                    .build();
        } catch (UnauthorisedException e) {
            throw new UnauthorisedException(e.getMessage());
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    private void handlePermissionAssignment(GrantPermissionToLeaderDto dto, EUrwego targetedOrganizationLevel) throws Exception {
        Optional<Permission> permissionOpt = permissionRepository.findByNationalId(dto.getNationalId());

        if (permissionOpt.isPresent()) {
            updateExistingPermission(permissionOpt.get(), dto, targetedOrganizationLevel);
        } else {
            createNewPermission(dto, targetedOrganizationLevel);
        }
    }

    private void updateExistingPermission(Permission existingPermission, GrantPermissionToLeaderDto dto, EUrwego targetedOrganizationLevel) throws Exception {
        updatePermissionAttributes(existingPermission, dto);
        Permission savedPermission = permissionRepository.save(existingPermission);

        if (savedPermission == null) {
            throw new Exception("Error while saving the permission...");
        }

//        User user = userRepository.findByNationalId(dto.getNationalId())
//                .orElseThrow(() -> new Exception("User not found."));
//        user.setRol(targetRole);
//        userRepository.save(user);
    }

    private void createNewPermission(GrantPermissionToLeaderDto dto, EUrwego targetedOrganizationLevel) throws Exception {
        Permission newPermission = convertDtoToEntity(dto);
        Permission savedPermission = permissionRepository.save(newPermission);

        if (savedPermission == null) {
            throw new Exception("Error while saving the permission...");
        }

//        User user = userRepository.findByNationalId(dto.getNationalId())
//                .orElseThrow(() -> new Exception("User not found."));
//        user.setRole(targetRole);
//        userRepository.save(user);
    }

    private void updatePermissionAttributes(Permission existingPermission, GrantPermissionToLeaderDto dto) {
        if (dto.getOrganizationLevel() != null) {
            existingPermission.setOrganizationLevel(dto.getOrganizationLevel());
        }
        if (dto.getLocation() != null) {
            existingPermission.setLocation(dto.getLocation());
        }
        if (dto.getCategory() != null) {
            existingPermission.setCategory(dto.getCategory());
        }
    }

    private Permission convertDtoToEntity(GrantPermissionToLeaderDto dto) {
        Permission permission = new Permission();
        permission.setNationalId(dto.getNationalId());
        permission.setLocation(dto.getLocation());
        permission.setCategory(dto.getCategory());
        permission.setOrganizationLevel(dto.getOrganizationLevel());
        permission.setVerified(false);
        return permission;
    }
}