package com.venefast.springboot.mcpserver.app.repositories;

import com.venefast.springboot.mcpserver.app.models.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for Movie entities.
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    /**
     * Finds movies matching the specified genre (case-insensitive).
     *
     * @param genre genre to match
     * @return list of matching movies
     */
    List<Movie> findByGenreIgnoreCase(String genre);

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
}
