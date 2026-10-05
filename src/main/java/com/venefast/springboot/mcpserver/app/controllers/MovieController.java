package com.venefast.springboot.mcpserver.app.controllers;

import com.venefast.springboot.mcpserver.app.models.dtos.MovieDto;
import com.venefast.springboot.mcpserver.app.services.MovieService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller exposing endpoints for Movie management.
 * Strictly communicates via MovieDto and delegates business logic to MovieService.
 */
@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Retrieves all movies in the catalog.
     *
     * @return 200 OK with list of MovieDto
     */
    @GetMapping
    public ResponseEntity<List<MovieDto>> getAllMovies() {
        return ResponseEntity.ok(movieService.findAll());
    }

    /**
     * Retrieves a movie by its ID.
     *
     * @param id movie ID
     * @return 200 OK with MovieDto if found, or 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable Long id) {
        return movieService.findById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Searches movies by title keyword.
     *
     * @param title search term
     * @return 200 OK with matching movies
     */
    @GetMapping("/search")
    public ResponseEntity<List<MovieDto>> searchMoviesByTitle(@RequestParam String title) {
        return ResponseEntity.ok(movieService.searchByTitle(title));
    }

    /**
     * Filters movies by genre / category.
     *
     * @param genre genre name
     * @return 200 OK with movies belonging to the genre
     */
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<MovieDto>> getMoviesByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(movieService.findByGenre(genre));
    }

    /**
     * Filters movies by audience classification.
     *
     * @param audience audience classification keyword
     * @return 200 OK with matching movies
     */
    @GetMapping("/audience/{audience}")
    public ResponseEntity<List<MovieDto>> getMoviesByAudience(@PathVariable String audience) {
        return ResponseEntity.ok(movieService.findByAudience(audience));
    }

    /**
     * Retrieves screening schedules for a specific movie.
     *
     * @param id movie ID
     * @return 200 OK with list of showtimes
     */
    @GetMapping("/{id}/schedules")
    public ResponseEntity<List<String>> getMovieSchedules(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieSchedules(id));
    }

    /**
     * Retrieves screening schedules for a movie by its title (case-insensitive).
     *
     * @param title movie title
     * @return 200 OK with schedule description or non-existence message
     */
    @GetMapping("/schedule")
    public ResponseEntity<String> getMovieScheduleByTitle(@RequestParam String title) {
        return ResponseEntity.ok(movieService.getMovieScheduleByTitle(title));
    }

    /**
     * Retrieves top-rated movies above or equal to a minimum rating threshold.
     *
     * @param minRating minimum rating (default 8.0)
     * @return 200 OK with list of top-rated movies
     */
    @GetMapping("/top-rated")
    public ResponseEntity<List<MovieDto>> getTopRatedMovies(
            @RequestParam(defaultValue = "8.0") Double minRating) {
        return ResponseEntity.ok(movieService.findTopRated(minRating));
    }

    /**
     * Creates a new movie.
     *
     * @param movieDto the movie payload to create
     * @return 201 Created with persisted MovieDto
     */
    @PostMapping
    public ResponseEntity<MovieDto> createMovie(@Valid @RequestBody MovieDto movieDto) {
        MovieDto created = movieService.create(movieDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Updates an existing movie.
     *
     * @param id movie ID to update
     * @param movieDto the updated movie payload
     * @return 200 OK with updated MovieDto
     */
    @PutMapping("/{id}")
    public ResponseEntity<MovieDto> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody MovieDto movieDto) {
        MovieDto updated = movieService.update(id, movieDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a movie by ID.
     *
     * @param id movie ID to delete
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
