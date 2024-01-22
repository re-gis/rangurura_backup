package com.backend.rangurura.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.rangurura.entities.Problem;
import com.backend.rangurura.enums.ECategory;
import com.backend.rangurura.enums.EUrwego;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    Problem[] findAllByOwner(String owner_id);

    List<Problem> findAllByUrwegoAndCategory(EUrwego origanizationLevel, ECategory category);
    
}
