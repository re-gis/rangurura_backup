package com.backend.rangurura.repositories;
import com.backend.rangurura.entities.Leaders;
import com.backend.rangurura.enums.EUrwego;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaderRepository  extends  JpaRepository<Leaders,Long>{

    Optional<Leaders>findByNationalId(String national_id);

    List<Leaders> findAllByLocationAndOriganizationLevel(String location, EUrwego origanizationLevel);
}
