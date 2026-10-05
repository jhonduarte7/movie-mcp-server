package com.venefast.springboot.mcpserver.app.mcp.tools;

import com.venefast.springboot.mcpserver.app.models.dtos.MovieCatalogDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieDto;
import com.venefast.springboot.mcpserver.app.services.MovieService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * MCP Tools for movie catalog browsing, searching, and queries.
 */
@Component
public class MovieCatalogTools {

    private final MovieService movieService;

    public MovieCatalogTools(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * MCP Tool: Searches movies by title keyword.
     */
    @McpTool(
        name = "searchMoviesByTitle",
        description = "Search for movies in the catalog whose title matches or contains the given search query."
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
        description = "Retrieve detailed information about a movie using its unique catalog ID."
    )
    public MovieDto getMovieById(
        @McpToolParam(description = "The unique numeric ID of the movie", required = true)
        Long id
    ) {
        return movieService.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Movie not found with id: " + id));
    }

    /**
     * MCP Tool: Retrieves movies belonging to a specified genre / category.
     */
    @McpTool(
        name = "getMoviesByGenre",
        description = "Retrieve all movies categorized under a specific genre such as Sci-Fi, Drama, Action, Crime, Animation, or Thriller."
    )
    public List<MovieDto> getMoviesByGenre(
        @McpToolParam(description = "The genre or category to filter by (e.g. Sci-Fi, Drama, Action)", required = true)
        String genre
    ) {
        return movieService.findByGenre(genre);
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
}
