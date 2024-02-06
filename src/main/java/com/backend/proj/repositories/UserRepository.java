package com.backend.proj.repositories;

import com.backend.proj.entities.User;
import com.backend.proj.enums.URole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNationalId(String nationalId);

    Optional<User> findOneByNationalId(String nationalId);

    Optional<User> findOneByPhone(String number);

    List<User> findByRole(URole role);;
}
