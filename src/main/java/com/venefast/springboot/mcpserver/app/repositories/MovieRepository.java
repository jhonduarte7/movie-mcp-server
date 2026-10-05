package com.venefast.springboot.mcpserver.app.repositories;

import com.venefast.springboot.mcpserver.app.models.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Movie entities.
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    /**
     * Finds movies matching the specified genre/category (case-insensitive) across the normalized genres relation.
     *
     * @param genre genre name to match
     * @return list of matching movies
     */
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.genres g WHERE LOWER(g.name) = LOWER(:genre)")
    List<Movie> findByGenreIgnoreCase(@Param("genre") String genre);

    /**
     * Finds the first movie matching the exact title ignoring case.
     *
     * @param title movie title
     * @return optional containing the matching movie
     */
    Optional<Movie> findFirstByTitleIgnoreCase(String title);

    /**
     * Finds movies whose title contains the given keyword (case-insensitive).
     *
     * @param title title search keyword
     * @return list of matching movies
     */
    List<Movie> findByTitleContainingIgnoreCase(String title);

    /**
     * Finds movies directed by directors matching the given keyword (case-insensitive).
     *
     * @param director director search keyword
     * @return list of matching movies
     */
    List<Movie> findByDirectorContainingIgnoreCase(String director);

    /**
     * Finds movies released in a specific year.
     *
     * @param releaseYear release year
     * @return list of matching movies
     */
    List<Movie> findByReleaseYear(Integer releaseYear);

    /**
     * Finds movies with a rating greater than or equal to the minimum rating, ordered descending.
     *
     * @param minRating minimum rating threshold
     * @return list of matching movies sorted by rating descending
     */
    List<Movie> findByRatingGreaterThanEqualOrderByRatingDesc(Double minRating);

    /**
     * Finds movies matching an audience classification (case-insensitive partial match).
     *
     * @param audience audience keyword
     * @return list of matching movies
     */
    @Query("SELECT DISTINCT m FROM Movie m WHERE LOWER(m.audience.name) LIKE LOWER(CONCAT('%', :audience, '%'))")
    List<Movie> findByAudienceContainingIgnoreCase(@Param("audience") String audience);
}
