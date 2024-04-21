package com.backend.proj.repositories;


import com.backend.proj.entities.Permission;
import com.backend.proj.enums.EUrwego;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;



public interface PermissionRepository   extends JpaRepository<Permission, UUID> {

    Optional<Permission> findByNationalId(String national_id);

    List<Permission> findAllByLocationAndOrganizationLevel(String location, EUrwego organizationLevel);

    Optional<Permission> findById(UUID id);
//    Optional<Leaders> findByNationalId(String nationalId);

//    Optional<Permission> findByNationalIdAndOrganizationLevel(String nationalId, EUrwego organizationLevel);


}