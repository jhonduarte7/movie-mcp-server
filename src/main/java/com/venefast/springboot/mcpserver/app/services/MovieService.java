package com.venefast.springboot.mcpserver.app.services;

import com.venefast.springboot.mcpserver.app.models.dtos.MovieDto;

import java.util.List;
import java.util.Optional;

/**
 * Service contract for Movie operations.
 * Operates strictly with DTOs, isolating persistence entities.
 */
public interface MovieService {

    /**
     * Retrieves all movies.
     *
     * @return list of movie DTOs
     */
    List<MovieDto> findAll();

    /**
     * Finds a movie by its unique identifier.
     *
     * @param id the movie ID
     * @return optional containing the movie DTO if found
     */
    Optional<MovieDto> findById(Long id);

    /**
     * Finds movies by genre.
     *
     * @param genre genre name
     * @return list of matching movie DTOs
     */
    List<MovieDto> findByGenre(String genre);

    /**
     * Searches movies whose title contains the query string.
     *
     * @param query title search query
     * @return list of matching movie DTOs
     */
    List<MovieDto> searchByTitle(String query);

    /**
     * Finds movies by director name keyword.
     *
     * @param director director search query
     * @return list of matching movie DTOs
     */
    List<MovieDto> findByDirector(String director);

    /**
     * Finds top-rated movies with rating >= minRating.
     *
     * @param minRating minimum rating threshold
     * @return list of matching movie DTOs ordered by rating descending
     */
    List<MovieDto> findTopRated(Double minRating);

    /**
     * Creates a new movie entry.
     *
     * @param movieDto the movie data to persist
     * @return the persisted movie DTO with generated ID
     */
    MovieDto create(MovieDto movieDto);

    /**
     * Updates an existing movie entry.
     *
     * @param id the movie ID to update
     * @param movieDto the new movie data
     * @return the updated movie DTO
     */
    MovieDto update(Long id, MovieDto movieDto);

    /**
     * Deletes a movie entry by ID.
     *
     * @param id the movie ID to delete
     */
    void delete(Long id);
}
