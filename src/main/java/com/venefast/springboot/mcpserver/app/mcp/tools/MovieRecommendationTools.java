package com.venefast.springboot.mcpserver.app.mcp.tools;

import com.venefast.springboot.mcpserver.app.models.dtos.MovieDto;
import com.venefast.springboot.mcpserver.app.services.MovieService;
import org.springframework.ai.mcp.annotation.McpPrompt;
import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MCP Component providing Movie Recommendation Prompts and Catalog Resources.
 */
@Component
public class MovieRecommendationTools {

    private final MovieService movieService;

    public MovieRecommendationTools(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * MCP Resource: Exposes catalog summary as a static MCP resource URI.
     */
    @McpResource(
        uri = "movies://catalog",
        name = "Movie Catalog Summary",
        description = "Textual summary of all available movies in the catalog with categories and showtimes.",
        mimeType = "text/plain"
    )
    public String getCatalogResource() {
        List<MovieDto> movies = movieService.findAll();
        return movies.stream()
            .map(m -> {
                String genresStr = (m.genres() != null && !m.genres().isEmpty())
                    ? String.join(", ", m.genres())
                    : m.genre();
                String schedStr = (m.schedules() != null && !m.schedules().isEmpty())
                    ? String.join(", ", m.schedules())
                    : "No showtimes";
                String audStr = (m.audience() != null) ? m.audience() : "N/A";
                String durStr = (m.duration() != null) ? m.duration() : "N/A";

                return String.format("[%d] %s (%d) - Directed by %s | %s | Rating: %.1f/10 | Audience: %s | Categories: %s | Showtimes: %s",
                    m.id(), m.title(), m.releaseYear(), m.director(), durStr, m.rating(), audStr, genresStr, schedStr);
            })
            .collect(Collectors.joining("\n"));
    }

    /**
     * MCP Prompt: Prompt template for AI movie recommendations.
     */
    @McpPrompt(
        name = "movieRecommendationPrompt",
        description = "Generates a prompt for recommending movies to a user based on genre preference and showtimes."
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
        sb.append("Here is the relevant cinema catalog from our database:\n");
        for (MovieDto m : catalog) {
            String genresStr = (m.genres() != null && !m.genres().isEmpty())
                ? String.join(", ", m.genres())
                : m.genre();
            String schedStr = (m.schedules() != null && !m.schedules().isEmpty())
                ? String.join(", ", m.schedules())
                : "TBD";

            sb.append("- ").append(m.title()).append(" (").append(m.releaseYear()).append(")")
              .append(" [").append(m.duration() != null ? m.duration() : "N/A").append("]")
              .append(", directed by ").append(m.director())
              .append(", rating: ").append(m.rating()).append("/10")
              .append(", audience: ").append(m.audience() != null ? m.audience() : "All")
              .append(", categories: ").append(genresStr)
              .append(", showtimes: ").append(schedStr)
              .append("\n  Synopsis: ").append(m.synopsis()).append("\n");
        }
        sb.append("\nPlease provide a personalized recommendation highlighting film themes, showtimes, and reasons to watch.");
        return sb.toString();
    }
}
