package com.venefast.springboot.mcpserver.app.mcp.tools;

import com.venefast.springboot.mcpserver.app.models.dtos.MovieScheduleDto;
import com.venefast.springboot.mcpserver.app.services.MovieService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MCP Tools for screening schedules and showtimes queries.
 * Operates with specialized MovieScheduleDto.
 */
@Component
public class MovieScheduleTools {

    private final MovieService movieService;

    public MovieScheduleTools(MovieService movieService) {
        this.movieService = movieService;
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
     * MCP Tool: Searches for a movie by name/title (case-insensitive) and returns its screening schedules.
     */
    @McpTool(
        name = "mcp_getMovieSchedule",
        description = "Search for a movie by its title or name (case-insensitive) and retrieve its screening showtimes and schedules. If the movie does not exist, returns a clear message stating it does not exist."
    )
    public String mcp_getMovieSchedule(
        @McpToolParam(description = "The movie title or name to search for (case-insensitive, e.g. Inception)", required = true)
        String movieName
    ) {
        return movieService.getMovieScheduleByTitle(movieName);
    }
}
