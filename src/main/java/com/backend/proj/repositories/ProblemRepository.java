package com.backend.proj.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.proj.entities.Problem;
import com.backend.proj.enums.ECategory;
import com.backend.proj.enums.EUrwego;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    Problem[] findAllByOwner(String owner_id);

    List<Problem> findAllByUrwegoAndCategory(EUrwego origanizationLevel, ECategory category);

    List<Problem> findAllByUrwegoAndCategoryAndTarget(EUrwego organizationLevel, ECategory category, String location);
    
}
