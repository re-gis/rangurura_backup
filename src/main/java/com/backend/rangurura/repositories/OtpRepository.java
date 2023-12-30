package com.backend.rangurura.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.rangurura.entities.Otp;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findOneByNumber(String phoneNumber);
    
}
