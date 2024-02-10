package com.backend.proj.repositories;
import com.backend.proj.entities.Leaders;
import com.backend.proj.entities.User;
import com.backend.proj.enums.EUrwego;

import com.backend.proj.enums.URole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LeaderRepository  extends  JpaRepository<Leaders,Long>{

    Optional<Leaders>findByNationalId(String national_id);

    List<Leaders> findAllByLocationAndOrganizationLevel(String location, EUrwego organizationLevel);






}
