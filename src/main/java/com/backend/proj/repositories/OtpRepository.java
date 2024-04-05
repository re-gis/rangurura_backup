package com.backend.proj.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.proj.entities.Otp;

public interface OtpRepository extends JpaRepository<Otp, UUID> {

    Optional<Otp> findOneByNumber(String phoneNumber);
    
}
