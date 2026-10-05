package com.venefast.springboot.mcpserver.app.mcp.tools;

import com.venefast.springboot.mcpserver.app.models.dtos.MovieCatalogDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieCreateDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieScheduleDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieUpdateDto;
import com.venefast.springboot.mcpserver.app.services.MovieService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * MCP Tools dedicated to Movie entity operations:
 * Browsing, searching, screening schedules, and full CRUD lifecycle management.
 */
@Component
public class MovieTools {

    private final MovieService movieService;

    public MovieTools(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * MCP Tool: Searches movies by title keyword.
     */
    @McpTool(
        name = "searchMoviesByTitle",
        description = "Search for movies in the catalog whose title matches or contains the given query."
    )
    public List<MovieDto> searchMoviesByTitle(
        @McpToolParam(description = "The title or partial title to search for (e.g. Inception)", required = true)
        String query
    ) {
        return movieService.searchByTitle(query);
    }

    /**
     * MCP Tool: Retrieves detailed movie information by movie ID.
     */
    @McpTool(
        name = "getMovieById",
        description = "Retrieve detailed information about a movie using its unique numeric ID."
    )
    public MovieDto getMovieById(
        @McpToolParam(description = "The unique numeric ID of the movie", required = true)
        Long id
    ) {
        return movieService.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Movie not found with id: " + id));
    }

    /**
     * MCP Tool: Retrieves highest rated movies with a minimum rating threshold.
     */
    @McpTool(
        name = "getTopRatedMovies",
        description = "Retrieve the highest rated movies in the catalog having a rating greater than or equal to the minimum rating."
    )
    public List<MovieDto> getTopRatedMovies(
        @McpToolParam(description = "Minimum rating threshold between 0.0 and 10.0 (defaults to 8.0)", required = false)
        Double minRating
    ) {
        double threshold = (minRating != null) ? minRating : 8.0;
        return movieService.findTopRated(threshold);
    }

    /**
     * MCP Tool: Lists all movies currently in the catalog.
     */
    @McpTool(
        name = "getAllMovies",
        description = "List all movies currently available in the catalog."
    )
    public List<MovieDto> getAllMovies() {
        return movieService.findAll();
    }

    /**
     * MCP Tool: Lists a lightweight catalog summary of all movies.
     */
    @McpTool(
        name = "getCatalogOverview",
        description = "Retrieve a simplified catalog overview containing movie title, duration, genres, audience classification, and rating."
    )
    public List<MovieCatalogDto> getCatalogOverview() {
        return movieService.findCatalogMovies();
    }

    /**
     * MCP Tool: Retrieves screening showtimes and schedules for a movie by ID.
     */
    @McpTool(
        name = "getMovieSchedules",
        description = "Retrieve the screening showtimes and schedules for a specific movie by its ID."
    )
    public List<String> getMovieSchedules(
        @McpToolParam(description = "The unique numeric ID of the movie", required = true)
        Long id
    ) {
        return movieService.getMovieSchedules(id);
    }

    /**
     * MCP Tool: Retrieves structured schedule details DTO for a movie by ID.
     */
    @McpTool(
        name = "getMovieScheduleDetails",
        description = "Retrieve structured movie schedule details including title, duration, audience rating, and showtimes by movie ID."
    )
    public MovieScheduleDto getMovieScheduleDetails(
        @McpToolParam(description = "The unique numeric ID of the movie", required = true)
        Long id
    ) {
        return movieService.getMovieScheduleDetails(id);
    }

    /**
     * MCP Tool: Searches for a movie by title and retrieves its screening showtimes.
     */
    @McpTool(
        name = "getMovieScheduleByTitle",
        description = "Retrieve screening showtimes and schedule details for a movie searching by title. If the movie does not exist, returns a clear explanatory message."
    )
    public String getMovieScheduleByTitle(
        @McpToolParam(description = "The movie title to search for (e.g. Inception, Moana 2)", required = true)
        String title
    ) {
        return movieService.getMovieScheduleByTitle(title);
    }

    /**
     * MCP Tool: Adds a new movie to the catalog using MovieCreateDto contract.
     */
    @McpTool(
        name = "addMovie",
        description = "Add a new movie to the catalog with title, director, genre, release year, rating, synopsis, audience, duration, and schedules."
    )
    public MovieDto addMovie(
        @McpToolParam(description = "The full title of the movie", required = true)
        String title,
        @McpToolParam(description = "Director of the movie", required = true)
        String director,
        @McpToolParam(description = "Genre or comma-separated categories (e.g. Sci-Fi, Action)", required = true)
        String genre,
        @McpToolParam(description = "Release year (e.g. 2024)", required = true)
        Integer releaseYear,
        @McpToolParam(description = "Rating from 0.0 to 10.0 (e.g. 8.5)", required = true)
        Double rating,
        @McpToolParam(description = "Brief plot summary or synopsis", required = false)
        String synopsis,
        @McpToolParam(description = "Audience classification (e.g. PG-13, R, TE)", required = false)
        String audience,
        @McpToolParam(description = "Duration in minutes (e.g. 148 min)", required = false)
        String duration,
        @McpToolParam(description = "Comma-separated showtimes (e.g. 14:00, 16:30, 19:00)", required = false)
        String schedules
    ) {
        List<String> genreList = (genre != null && !genre.isBlank())
            ? Arrays.stream(genre.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList()
            : List.of();

        List<String> schedulesList = (schedules != null && !schedules.isBlank())
            ? Arrays.stream(schedules.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList()
            : List.of("14:00", "17:00", "20:00");

        MovieCreateDto createDto = new MovieCreateDto(
            title,
            director,
            (duration != null && !duration.isBlank()) ? duration : "120 min",
            genre,
            genreList,
            (audience != null && !audience.isBlank()) ? audience : "TE - Todo Espectador",
            schedulesList,
            releaseYear,
            rating,
            synopsis
        );
        return movieService.create(createDto);
    }

    /**
     * Convenience Java overload for creating movie with primary parameters.
     */
    public MovieDto addMovie(String title, String director, String genre, Integer releaseYear, Double rating, String synopsis) {
        return addMovie(title, director, genre, releaseYear, rating, synopsis, null, null, null);
    }

    /**
     * MCP Tool: Updates an existing movie in the catalog using MovieUpdateDto contract.
     */
    @McpTool(
        name = "updateMovie",
        description = "Update an existing movie in the catalog by its ID with new title, director, genre, release year, rating, and synopsis."
    )
    public MovieDto updateMovie(
        @McpToolParam(description = "The unique numeric ID of the movie to update", required = true)
        Long id,
        @McpToolParam(description = "The updated title of the movie", required = true)
        String title,
        @McpToolParam(description = "Updated director of the movie", required = true)
        String director,
        @McpToolParam(description = "Updated genre of the movie (e.g. Sci-Fi, Action, Drama)", required = true)
        String genre,
        @McpToolParam(description = "Updated release year (e.g. 2024)", required = true)
        Integer releaseYear,
        @McpToolParam(description = "Updated rating from 0.0 to 10.0 (e.g. 8.5)", required = true)
        Double rating,
        @McpToolParam(description = "Updated brief plot summary or synopsis", required = false)
        String synopsis
    ) {
        List<String> genreList = (genre != null && !genre.isBlank())
            ? Arrays.stream(genre.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList()
            : List.of();

        MovieUpdateDto updateDto = new MovieUpdateDto(
            title,
            director,
            "120 min",
            genre,
            genreList,
            null,
            List.of(),
            releaseYear,
            rating,
            synopsis
        );
        return movieService.update(id, updateDto);
    }

    /**
     * MCP Tool: Deletes a movie from the catalog by ID.
     */
    @McpTool(
        name = "deleteMovie",
        description = "Remove a movie from the catalog by its ID."
    )
    public String deleteMovie(
        @McpToolParam(description = "The unique numeric ID of the movie to delete", required = true)
        Long id
    ) {
        movieService.delete(id);
        return "Movie with ID " + id + " was successfully deleted.";
    }
}
