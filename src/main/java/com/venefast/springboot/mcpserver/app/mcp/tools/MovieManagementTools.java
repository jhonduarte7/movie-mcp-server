package com.venefast.springboot.mcpserver.app.mcp.tools;

import com.venefast.springboot.mcpserver.app.models.dtos.MovieCreateDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieUpdateDto;
import com.venefast.springboot.mcpserver.app.services.MovieService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * MCP Tools for movie management (creation, update, deletion).
 * Delegates to specialized MovieCreateDto and MovieUpdateDto contracts.
 */
@Component
public class MovieManagementTools {

    private final MovieService movieService;

    public MovieManagementTools(MovieService movieService) {
        this.movieService = movieService;
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
