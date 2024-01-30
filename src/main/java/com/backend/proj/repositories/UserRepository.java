package com.backend.proj.repositories;

import com.backend.proj.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNationalId(String nationalId);

    Optional<User> findOneByNationalId(String nationalId);

    Optional<User> findOneByPhone(String number);
}
