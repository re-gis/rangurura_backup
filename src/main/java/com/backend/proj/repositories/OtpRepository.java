package com.backend.proj.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.proj.entities.Otp;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findOneByNumber(String phoneNumber);
    
}
