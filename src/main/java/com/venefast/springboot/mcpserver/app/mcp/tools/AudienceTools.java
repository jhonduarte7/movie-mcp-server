package com.venefast.springboot.mcpserver.app.mcp.tools;

import com.venefast.springboot.mcpserver.app.models.dtos.MovieDto;
import com.venefast.springboot.mcpserver.app.services.MovieService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MCP Tools dedicated to Audience entity operations:
 * Listing audience age classifications and filtering movies by classification.
 */
@Component
public class AudienceTools {

    private final MovieService movieService;

    public AudienceTools(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * MCP Tool: Lists all available audience age classifications in the catalog.
     */
    @McpTool(
        name = "getAllAudiences",
        description = "Retrieve all available audience and age classification ratings in the catalog (e.g. PG-13, R, TE - Todo Espectador, +14, +18)."
    )
    public List<String> getAllAudiences() {
        return movieService.findAllAudiences();
    }

    /**
     * MCP Tool: Retrieves movies matching an audience classification rating.
     */
    @McpTool(
        name = "getMoviesByAudience",
        description = "Retrieve all movies categorized under a specific audience classification (e.g. PG-13, R, TE - Todo Espectador, +14, +18)."
    )
    public List<MovieDto> getMoviesByAudience(
        @McpToolParam(description = "Audience classification rating keyword (e.g. PG-13, R, TE, +14, +18)", required = true)
        String audience
    ) {
        return movieService.findByAudience(audience);
    }
}
