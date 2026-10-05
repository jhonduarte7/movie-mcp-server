package com.venefast.springboot.mcpserver.app.models.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Collections;
import java.util.List;

/**
 * Specialized DTO record for Movie update requests.
 * Encapsulates input payload for updating existing movie records.
 * Used primarily by MovieManagementTools and update REST endpoints.
 */
public record MovieUpdateDto(
    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must not exceed 150 characters")
    String title,

    @NotBlank(message = "Director is required")
    @Size(max = 120, message = "Director must not exceed 120 characters")
    String director,

    @Size(max = 50, message = "Duration must not exceed 50 characters")
    String duration,

    String genre,

    List<String> genres,

    String audience,

    List<String> schedules,

    @NotNull(message = "Release year is required")
    @Min(value = 1888, message = "Release year must be 1888 or later")
    @Max(value = 2100, message = "Release year cannot be in the far future")
    Integer releaseYear,

    @NotNull(message = "Rating is required")
    @DecimalMin(value = "0.0", message = "Rating must be at least 0.0")
    @DecimalMax(value = "10.0", message = "Rating must be at most 10.0")
    Double rating,

    @Size(max = 2000, message = "Synopsis must not exceed 2000 characters")
    String synopsis
) {
    /**
     * Convenience constructor with primary fields.
     */
    public MovieUpdateDto(String title, String director, String genre, Integer releaseYear, Double rating, String synopsis) {
        this(
            title,
            director,
            "120 min",
            genre,
            (genre != null && !genre.isBlank()) ? List.of(genre) : Collections.emptyList(),
            null,
            Collections.emptyList(),
            releaseYear,
            rating,
            synopsis
        );
    }
}
