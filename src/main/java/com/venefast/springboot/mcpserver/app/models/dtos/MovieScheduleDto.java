package com.venefast.springboot.mcpserver.app.models.dtos;

import java.util.Collections;
import java.util.List;

/**
 * Specialized DTO record representing screening schedules and showtimes for a movie.
 * Used primarily by MovieScheduleTools and schedule query endpoints.
 */
public record MovieScheduleDto(
    Long movieId,
    String title,
    String duration,
    String audience,
    List<String> schedules,
    String message
) {
    /**
     * Factory helper for a found movie with schedules.
     */
    public static MovieScheduleDto of(Long movieId, String title, String duration, String audience, List<String> schedules) {
        String msg = (schedules != null && !schedules.isEmpty())
            ? "Screening schedules for '" + title + "': " + String.join(", ", schedules)
            : "Movie '" + title + "' exists in the catalog, but has no screening schedules currently scheduled.";
        return new MovieScheduleDto(
            movieId,
            title,
            duration,
            audience,
            (schedules != null ? schedules : Collections.emptyList()),
            msg
        );
    }

    /**
     * Overloaded factory helper without explicit ID.
     */
    public static MovieScheduleDto of(String title, String duration, String audience, List<String> schedules) {
        return of(1L, title, duration, audience, schedules);
    }

    /**
     * Factory helper for when a movie does not exist.
     */
    public static MovieScheduleDto notFound(String searchedTitle) {
        return new MovieScheduleDto(
            null,
            searchedTitle,
            null,
            null,
            Collections.emptyList(),
            "The movie '" + searchedTitle + "' does not exist in the catalog."
        );
    }

    /**
     * Checks if the movie was found and has valid schedules.
     */
    public boolean available() {
        return movieId != null;
    }
}
