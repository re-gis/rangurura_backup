package com.backend.proj.repositories;

import com.backend.proj.entities.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Events , UUID> {
    Events[] findAllByOwner(String owner_id);

    @Query(value = "SELECT * FROM events WHERE (location = :village AND organization_level='UMUDUGUDU') OR " +
            "(location = :sector AND organization_level='UMURENGE') OR " +
            "(location = :cell AND organization_level='AKAGARI') OR " +
            "(location = :district AND organization_level='AKARERE') OR " +
            "(location = :province AND organization_level='INTARA')", nativeQuery = true)
    List<Events> findAllByOrganizationLevel(
            String village, String sector, String cell, String district, String province);

    Optional<Events> findById(UUID id);

    void deleteById(UUID id);

}
