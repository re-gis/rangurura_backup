package com.backend.rangurura.repositories;

import com.backend.rangurura.entities.Suggestions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SuggestionRepository extends JpaRepository <Suggestions , Long> {

    Optional<Suggestions> findOneByPhoneNumber(String phoneNumber);
}
