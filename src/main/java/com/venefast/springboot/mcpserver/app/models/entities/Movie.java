package com.venefast.springboot.mcpserver.app.models.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JPA Entity representing a movie in the database with normalized relationships
 * for Audience, Genres/Categories, and Screening Schedules.
 */
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 120)
    private String director;

    @Column(length = 50)
    private String duration;

    @Column(name = "release_year", nullable = false)
    private Integer releaseYear;

    @Column(nullable = false)
    private Double rating;

    @Column(length = 2000)
    private String synopsis;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "audience_id")
    private Audience audience;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "movie_genres",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "movie_schedules", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "schedule_time", length = 30)
    private List<String> schedules = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Movie() {
    }

    /**
     * Constructor matching normalized seeder pattern with String rating (e.g. "4.8/5" or "8.8").
     */
    public Movie(String title, String duration, String rating, Audience audience, Set<Genre> genres, List<String> schedules) {
        this.title = title;
        this.director = "Unknown";
        this.duration = duration;
        this.releaseYear = 2024;
        this.rating = parseRatingString(rating);
        this.synopsis = "";
        this.audience = audience;
        this.genres = (genres != null) ? genres : new LinkedHashSet<>();
        this.schedules = (schedules != null) ? schedules : new ArrayList<>();
    }

    /**
     * Constructor with numeric rating and normalized relations.
     */
    public Movie(String title, String duration, Double rating, Audience audience, Set<Genre> genres, List<String> schedules) {
        this.title = title;
        this.director = "Unknown";
        this.duration = duration;
        this.releaseYear = 2024;
        this.rating = (rating != null) ? rating : 8.0;
        this.synopsis = "";
        this.audience = audience;
        this.genres = (genres != null) ? genres : new LinkedHashSet<>();
        this.schedules = (schedules != null) ? schedules : new ArrayList<>();
    }

    /**
     * Comprehensive constructor with all movie metadata and normalized relations.
     */
    public Movie(String title, String director, String duration, Integer releaseYear, Double rating,
                 String synopsis, Audience audience, Set<Genre> genres, List<String> schedules) {
        this.title = title;
        this.director = director;
        this.duration = duration;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.synopsis = synopsis;
        this.audience = audience;
        this.genres = (genres != null) ? genres : new LinkedHashSet<>();
        this.schedules = (schedules != null) ? schedules : new ArrayList<>();
    }

    /**
     * Backwards-compatible constructor for existing tests and simple creations.
     */
    public Movie(String title, String director, String genre, Integer releaseYear, Double rating, String synopsis) {
        this.title = title;
        this.director = director;
        this.duration = "120 min";
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.synopsis = synopsis;
        if (genre != null && !genre.isBlank()) {
            this.genres.add(new Genre(genre.trim()));
        }
    }

    private static Double parseRatingString(String ratingStr) {
        if (ratingStr == null || ratingStr.isBlank()) {
            return 8.0;
        }
        try {
            if (ratingStr.contains("/")) {
                String[] parts = ratingStr.split("/");
                double val = Double.parseDouble(parts[0].trim());
                double max = Double.parseDouble(parts[1].trim());
                if (max == 5.0) {
                    return Math.round((val * 2.0) * 10.0) / 10.0;
                }
                return val;
            }
            return Double.parseDouble(ratingStr.trim());
        } catch (Exception e) {
            return 8.0;
        }
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public Audience getAudience() {
        return audience;
    }

    public void setAudience(Audience audience) {
        this.audience = audience;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public List<String> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<String> schedules) {
        this.schedules = schedules;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Helper to get primary genre name or comma-separated names for backwards compatibility.
     */
    public String getGenre() {
        if (genres == null || genres.isEmpty()) {
            return "";
        }
        return genres.stream().map(Genre::getName).collect(Collectors.joining(", "));
    }

    public void setGenre(String genreName) {
        if (this.genres == null) {
            this.genres = new LinkedHashSet<>();
        }
        this.genres.clear();
        if (genreName != null && !genreName.isBlank()) {
            this.genres.add(new Genre(genreName.trim()));
        }
    }
}
