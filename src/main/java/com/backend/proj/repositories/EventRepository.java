package com.backend.proj.repositories;

import com.backend.proj.entities.Events;
import com.backend.proj.enums.EUrwego;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Events , Long> {
    Events[] findAllByOwner(String owner_id);
    List<Events> findAllByOrganizationLevel(String organizationLevel);

}
