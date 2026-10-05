package com.venefast.springboot.mcpserver.app.mcp;

import com.venefast.springboot.mcpserver.app.models.dtos.MovieDto;
import com.venefast.springboot.mcpserver.app.services.MovieService;
import org.springframework.ai.mcp.annotation.McpPrompt;
import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Model Context Protocol (MCP) component exposing Movie management tools, resources, and prompts
 * to AI models and assistants via Spring AI MCP Server.
 */
@Component
public class MovieMcpTools {

    private final MovieService movieService;

    public MovieMcpTools(MovieService movieService) {
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
     * MCP Tool: Retrieves movies belonging to a specified genre.
     */
    @McpTool(
        name = "getMoviesByGenre",
        description = "Retrieve all movies categorized under a specific genre such as Sci-Fi, Drama, Action, Crime, or Animation."
    )
    public List<MovieDto> getMoviesByGenre(
        @McpToolParam(description = "The genre to filter by (e.g. Sci-Fi, Drama, Action)", required = true)
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
     * MCP Tool: Adds a new movie to the catalog.
     */
    @McpTool(
        name = "addMovie",
        description = "Add a new movie to the catalog with title, director, genre, release year, rating, and synopsis."
    )
    public MovieDto addMovie(
        @McpToolParam(description = "The full title of the movie", required = true)
        String title,
        @McpToolParam(description = "Director of the movie", required = true)
        String director,
        @McpToolParam(description = "Genre of the movie (e.g. Sci-Fi, Action, Drama)", required = true)
        String genre,
        @McpToolParam(description = "Release year (e.g. 2024)", required = true)
        Integer releaseYear,
        @McpToolParam(description = "Rating from 0.0 to 10.0 (e.g. 8.5)", required = true)
        Double rating,
        @McpToolParam(description = "Brief plot summary or synopsis", required = false)
        String synopsis
    ) {
        MovieDto newMovie = new MovieDto(title, director, genre, releaseYear, rating, synopsis);
        return movieService.create(newMovie);
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

    /**
     * MCP Resource: Exposes catalog summary as a static MCP resource URI.
     */
    @McpResource(
        uri = "movies://catalog",
        name = "Movie Catalog Summary",
        description = "Textual summary of all available movies in the catalog.",
        mimeType = "text/plain"
    )
    public String getCatalogResource() {
        List<MovieDto> movies = movieService.findAll();
        return movies.stream()
            .map(m -> String.format("[%d] %s (%d) - Directed by %s | Genre: %s | Rating: %.1f/10",
                m.id(), m.title(), m.releaseYear(), m.director(), m.genre(), m.rating()))
            .collect(Collectors.joining("\n"));
    }

    /**
     * MCP Prompt: Prompt template for AI movie recommendations.
     */
    @McpPrompt(
        name = "movieRecommendationPrompt",
        description = "Generates a prompt for recommending movies to a user based on genre preference."
    )
    public String movieRecommendationPrompt(
        @McpToolParam(description = "Preferred genre for recommendations", required = false)
        String genre
    ) {
        String targetGenre = (genre != null && !genre.isBlank()) ? genre : "any";
        List<MovieDto> catalog = (genre != null && !genre.isBlank())
            ? movieService.findByGenre(genre)
            : movieService.findAll();

        StringBuilder sb = new StringBuilder();
        sb.append("You are an expert film critic and movie recommender.\n");
        sb.append("The user is looking for recommendations in the genre: ").append(targetGenre).append(".\n\n");
        sb.append("Here is the relevant catalog from our database:\n");
        for (MovieDto m : catalog) {
            sb.append("- ").append(m.title()).append(" (").append(m.releaseYear()).append(")")
              .append(", directed by ").append(m.director())
              .append(", rating: ").append(m.rating()).append("/10: ")
              .append(m.synopsis()).append("\n");
        }
        sb.append("\nPlease provide a personalized recommendation highlighting key themes, cinematography, and why the viewer would enjoy them.");
        return sb.toString();
    }
}
