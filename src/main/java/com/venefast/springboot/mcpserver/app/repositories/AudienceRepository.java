package com.venefast.springboot.mcpserver.app.repositories;

import com.venefast.springboot.mcpserver.app.models.entities.Audience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for Audience classification entities.
 */
@Repository
public interface AudienceRepository extends JpaRepository<Audience, Long> {

    /**
     * Finds an audience entry by its exact name.
     *
     * @param name audience name
     * @return optional containing matching Audience
     */
    Optional<Audience> findByName(String name);

    /**
     * Finds an audience entry by name ignoring case.
     *
     * @param name audience name
     * @return optional containing matching Audience
     */
    Optional<Audience> findByNameIgnoreCase(String name);
}
