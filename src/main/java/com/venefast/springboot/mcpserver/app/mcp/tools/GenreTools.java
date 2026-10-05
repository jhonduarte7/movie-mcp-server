package com.venefast.springboot.mcpserver.app.mcp.tools;

import com.venefast.springboot.mcpserver.app.models.dtos.MovieDto;
import com.venefast.springboot.mcpserver.app.services.MovieService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MCP Tools dedicated to Genre entity operations:
 * Listing catalog genres/categories and filtering movies by genre.
 */
@Component
public class GenreTools {

    private final MovieService movieService;

    public GenreTools(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * MCP Tool: Lists all available movie genres/categories in the cinema catalog.
     */
    @McpTool(
        name = "getAllGenres",
        description = "Retrieve all available movie genres and categories in the cinema catalog (e.g. Sci-Fi, Drama, Animation, Action)."
    )
    public List<String> getAllGenres() {
        return movieService.findAllGenres();
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
}
