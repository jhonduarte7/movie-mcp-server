package com.venefast.springboot.mcpserver.app.repositories;

import com.venefast.springboot.mcpserver.app.models.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for Genre / Movie Category entities.
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    /**
     * Finds a genre entry by its exact name.
     *
     * @param name genre name
     * @return optional containing matching Genre
     */
    Optional<Genre> findByName(String name);

    /**
     * Finds a genre entry by name ignoring case.
     *
     * @param name genre name
     * @return optional containing matching Genre
     */
    Optional<Genre> findByNameIgnoreCase(String name);
}
