package com.backend.rangurura.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.rangurura.entities.Problem;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    
}
