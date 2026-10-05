package com.venefast.springboot.mcpserver.app.services;

import com.venefast.springboot.mcpserver.app.models.dtos.MovieCatalogDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieCreateDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieScheduleDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieUpdateDto;

import java.util.List;
import java.util.Optional;

/**
 * Service contract for Movie operations.
 * Operates strictly with modular DTOs, isolating persistence entities.
 */
public interface MovieService {

    /**
     * Retrieves all movies.
     *
     * @return list of movie DTOs
     */
    List<MovieDto> findAll();

    /**
     * Retrieves all movies formatted for catalog browsing.
     *
     * @return list of MovieCatalogDto
     */
    List<MovieCatalogDto> findCatalogMovies();

    /**
     * Finds a movie by its unique identifier.
     *
     * @param id the movie ID
     * @return optional containing the movie DTO if found
     */
    Optional<MovieDto> findById(Long id);

    /**
     * Finds movies by genre/category.
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
     * Finds movies by audience rating/classification keyword (e.g. PG-13, R, TE).
     *
     * @param audience audience keyword
     * @return list of matching movie DTOs
     */
    List<MovieDto> findByAudience(String audience);

    /**
     * Retrieves screening schedules / showtimes for a specific movie.
     *
     * @param id movie ID
     * @return list of showtime strings
     */
    List<String> getMovieSchedules(Long id);

    /**
     * Retrieves structured schedule details DTO for a movie by ID.
     *
     * @param id movie ID
     * @return MovieScheduleDto
     */
    MovieScheduleDto getMovieScheduleDetails(Long id);

    /**
     * Retrieves structured schedule details DTO for a movie by title (case-insensitive).
     *
     * @param title movie title
     * @return MovieScheduleDto with details or clear non-existence message
     */
    MovieScheduleDto getMovieScheduleDetailsByTitle(String title);

    /**
     * Retrieves screening schedules for a movie by title (case-insensitive).
     * Returns a clear message if the movie does not exist.
     *
     * @param title movie title
     * @return screening schedule description or clear non-existence message
     */
    String getMovieScheduleByTitle(String title);

    /**
     * Creates a new movie entry from a general MovieDto.
     *
     * @param movieDto the movie data to persist
     * @return the persisted movie DTO with generated ID
     */
    MovieDto create(MovieDto movieDto);

    /**
     * Creates a new movie entry from a specialized MovieCreateDto.
     *
     * @param createDto the creation payload contract
     * @return the persisted movie DTO with generated ID
     */
    MovieDto create(MovieCreateDto createDto);

    /**
     * Updates an existing movie entry from a general MovieDto.
     *
     * @param id the movie ID to update
     * @param movieDto the new movie data
     * @return the updated movie DTO
     */
    MovieDto update(Long id, MovieDto movieDto);

    /**
     * Updates an existing movie entry from a specialized MovieUpdateDto.
     *
     * @param id the movie ID to update
     * @param updateDto the update payload contract
     * @return the updated movie DTO
     */
    MovieDto update(Long id, MovieUpdateDto updateDto);

    /**
     * Deletes a movie entry by ID.
     *
     * @param id the movie ID to delete
     */
    void delete(Long id);
}
