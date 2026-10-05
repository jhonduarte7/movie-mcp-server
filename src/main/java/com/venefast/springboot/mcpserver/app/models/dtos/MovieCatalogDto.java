package com.venefast.springboot.mcpserver.app.models.dtos;

import java.util.Collections;
import java.util.List;

/**
 * Specialized DTO record representing a movie item in catalog browsing and search listings.
 * Used primarily by MovieCatalogTools.
 */
public record MovieCatalogDto(
    Long id,
    String title,
    String director,
    String duration,
    String genre,
    List<String> genres,
    String audience,
    Integer releaseYear,
    Double rating,
    String synopsis
) {
    /**
     * Convenience constructor with primary fields.
     */
    public MovieCatalogDto(Long id, String title, String director, String genre, Integer releaseYear, Double rating, String synopsis) {
        this(
            id,
            title,
            director,
            "120 min",
            genre,
            (genre != null && !genre.isBlank()) ? List.of(genre) : Collections.emptyList(),
            null,
            releaseYear,
            rating,
            synopsis
        );
    }

    /**
     * Convenience constructor for catalog overview cards.
     */
    public MovieCatalogDto(Long id, String title, String duration, List<String> genres, String audience, Double rating) {
        this(
            id,
            title,
            null,
            duration,
            (genres != null && !genres.isEmpty()) ? genres.get(0) : null,
            (genres != null ? genres : Collections.emptyList()),
            audience,
            null,
            rating,
            null
        );
    }

    /**
     * Convenience constructor with audience first.
     */
    public MovieCatalogDto(Long id, String title, String duration, String audience, List<String> genres, Double rating) {
        this(id, title, duration, genres, audience, rating);
    }
}
