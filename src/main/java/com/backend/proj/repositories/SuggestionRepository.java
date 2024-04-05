package com.backend.proj.repositories;

import com.backend.proj.entities.Suggestions;
import com.backend.proj.enums.ECategory;
import com.backend.proj.enums.EUrwego;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SuggestionRepository extends JpaRepository <Suggestions , UUID> {

    Optional<Suggestions> findOneByPhoneNumber(String phoneNumber);

    List<Suggestions> findAllByNationalId(String nationalId);

    List<Suggestions> findAllByUrwegoAndLocationAndCategory(EUrwego origanizationLevel, String location,
            ECategory category);

    Optional<Suggestions> findById(UUID id);
}
