package com.venefast.springboot.mcpserver.app.models.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Immutable DTO record representing movie payload contract.
 * Excludes internal database audit fields (createdAt, updatedAt).
 */
public record MovieDto(
    Long id,

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must not exceed 150 characters")
    String title,

    @NotBlank(message = "Director is required")
    @Size(max = 120, message = "Director must not exceed 120 characters")
    String director,

    @NotBlank(message = "Genre is required")
    @Size(max = 60, message = "Genre must not exceed 60 characters")
    String genre,

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
     * Compact constructor helper for creating a MovieDto without an ID (e.g. for creation requests).
     */
    public MovieDto(String title, String director, String genre, Integer releaseYear, Double rating, String synopsis) {
        this(null, title, director, genre, releaseYear, rating, synopsis);
    }
}
