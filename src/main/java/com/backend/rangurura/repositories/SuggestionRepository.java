package com.backend.rangurura.repositories;

import com.backend.rangurura.entities.Suggestions;
import com.backend.rangurura.enums.ECategory;
import com.backend.rangurura.enums.EUrwego;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SuggestionRepository extends JpaRepository <Suggestions , Long> {

    Optional<Suggestions> findOneByPhoneNumber(String phoneNumber);

    List<Suggestions> findAllByNationalId(String nationalId);

    List<Suggestions> findAllByUrwegoAndLocationAndCategory(EUrwego origanizationLevel, String location,
            ECategory category);
}
