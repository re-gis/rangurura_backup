package com.backend.proj.repositories;

import com.backend.proj.entities.Leaders;
import com.backend.proj.enums.EUrwego;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaderRepository extends JpaRepository<Leaders, UUID> {

    Optional<Leaders> findByNationalId(String national_id);

    List<Leaders> findAllByLocationAndOrganizationLevel(String location, EUrwego organizationLevel);

    Optional<Leaders> findById(UUID id);
    // Optional<Leaders> findByNationalId(String nationalId);

}
