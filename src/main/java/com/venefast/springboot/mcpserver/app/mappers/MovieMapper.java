package com.venefast.springboot.mcpserver.app.mappers;

import com.venefast.springboot.mcpserver.app.models.dtos.MovieCatalogDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieCreateDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieScheduleDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieUpdateDto;
import com.venefast.springboot.mcpserver.app.models.entities.Audience;
import com.venefast.springboot.mcpserver.app.models.entities.Genre;
import com.venefast.springboot.mcpserver.app.models.entities.Movie;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Dedicated mapper component for bidirectional conversions between Movie entity and specialized DTO records:
 * - MovieDto (general full representation)
 * - MovieCatalogDto (browsing & search queries)
 * - MovieScheduleDto (showtimes & screening schedules)
 * - MovieCreateDto (creation command contract)
 * - MovieUpdateDto (update command contract)
 */
@Component
public class MovieMapper {

    /**
     * Converts a Movie JPA Entity into an immutable MovieDto record.
     *
     * @param entity the JPA entity
     * @return the corresponding DTO, or null if entity is null
     */
    public MovieDto toDto(Movie entity) {
        if (entity == null) {
            return null;
        }

        List<String> genreNames = extractGenreNames(entity);
        String primaryGenre = !genreNames.isEmpty() ? genreNames.get(0) : "";
        String audienceName = (entity.getAudience() != null) ? entity.getAudience().getName() : null;
        List<String> schedulesList = (entity.getSchedules() != null)
            ? new ArrayList<>(entity.getSchedules())
            : Collections.emptyList();

        return new MovieDto(
            entity.getId(),
            entity.getTitle(),
            entity.getDirector(),
            entity.getDuration(),
            primaryGenre,
            genreNames,
            audienceName,
            schedulesList,
            entity.getReleaseYear(),
            entity.getRating(),
            entity.getSynopsis()
        );
    }

    /**
     * Converts a Movie JPA Entity into a specialized MovieCatalogDto.
     *
     * @param entity the JPA entity
     * @return the catalog DTO, or null if entity is null
     */
    public MovieCatalogDto toCatalogDto(Movie entity) {
        if (entity == null) {
            return null;
        }

        List<String> genreNames = extractGenreNames(entity);
        String primaryGenre = !genreNames.isEmpty() ? genreNames.get(0) : "";
        String audienceName = (entity.getAudience() != null) ? entity.getAudience().getName() : null;

        return new MovieCatalogDto(
            entity.getId(),
            entity.getTitle(),
            entity.getDirector(),
            entity.getDuration(),
            primaryGenre,
            genreNames,
            audienceName,
            entity.getReleaseYear(),
            entity.getRating(),
            entity.getSynopsis()
        );
    }

    /**
     * Converts a Movie JPA Entity into a specialized MovieScheduleDto.
     *
     * @param entity the JPA entity
     * @return the schedule DTO, or null if entity is null
     */
    public MovieScheduleDto toScheduleDto(Movie entity) {
        if (entity == null) {
            return null;
        }
        String audienceName = (entity.getAudience() != null) ? entity.getAudience().getName() : null;
        List<String> schedulesList = (entity.getSchedules() != null)
            ? new ArrayList<>(entity.getSchedules())
            : Collections.emptyList();

        return MovieScheduleDto.of(
            entity.getId(),
            entity.getTitle(),
            entity.getDuration(),
            audienceName,
            schedulesList
        );
    }

    /**
     * Converts an immutable MovieDto record into a Movie JPA Entity.
     *
     * @param dto the DTO record
     * @return the corresponding JPA entity, or null if dto is null
     */
    public Movie toEntity(MovieDto dto) {
        if (dto == null) {
            return null;
        }

        Movie movie = new Movie();
        movie.setId(dto.id());
        movie.setTitle(dto.title());
        movie.setDirector(dto.director());
        movie.setDuration((dto.duration() != null && !dto.duration().isBlank()) ? dto.duration() : "120 min");
        movie.setReleaseYear(dto.releaseYear());
        movie.setRating(dto.rating());
        movie.setSynopsis(dto.synopsis());

        if (dto.audience() != null && !dto.audience().isBlank()) {
            movie.setAudience(new Audience(dto.audience().trim()));
        }

        movie.setGenres(buildGenres(dto.genres(), dto.genre()));

        if (dto.schedules() != null) {
            movie.setSchedules(new ArrayList<>(dto.schedules()));
        }

        return movie;
    }

    /**
     * Converts a specialized MovieCreateDto into a new Movie JPA Entity.
     *
     * @param dto the create DTO
     * @return the corresponding JPA entity, or null if dto is null
     */
    public Movie toEntity(MovieCreateDto dto) {
        if (dto == null) {
            return null;
        }

        Movie movie = new Movie();
        movie.setTitle(dto.title());
        movie.setDirector(dto.director());
        movie.setDuration((dto.duration() != null && !dto.duration().isBlank()) ? dto.duration() : "120 min");
        movie.setReleaseYear(dto.releaseYear());
        movie.setRating(dto.rating());
        movie.setSynopsis(dto.synopsis());

        if (dto.audience() != null && !dto.audience().isBlank()) {
            movie.setAudience(new Audience(dto.audience().trim()));
        }

        movie.setGenres(buildGenres(dto.genres(), dto.genre()));

        if (dto.schedules() != null) {
            movie.setSchedules(new ArrayList<>(dto.schedules()));
        }

        return movie;
    }

    /**
     * Updates an existing Movie JPA entity from a MovieUpdateDto payload.
     *
     * @param dto the update DTO
     * @param entity the target JPA entity to update
     */
    public void updateEntityFromDto(MovieUpdateDto dto, Movie entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setTitle(dto.title());
        entity.setDirector(dto.director());
        if (dto.duration() != null && !dto.duration().isBlank()) {
            entity.setDuration(dto.duration());
        }
        entity.setReleaseYear(dto.releaseYear());
        entity.setRating(dto.rating());
        entity.setSynopsis(dto.synopsis());

        if (dto.audience() != null && !dto.audience().isBlank()) {
            if (entity.getAudience() != null) {
                entity.getAudience().setName(dto.audience().trim());
            } else {
                entity.setAudience(new Audience(dto.audience().trim()));
            }
        }

        if (dto.genres() != null && !dto.genres().isEmpty()) {
            Set<Genre> newGenres = dto.genres().stream()
                .filter(g -> g != null && !g.isBlank())
                .map(g -> new Genre(g.trim()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
            entity.setGenres(newGenres);
        } else if (dto.genre() != null && !dto.genre().isBlank()) {
            entity.setGenre(dto.genre().trim());
        }

        if (dto.schedules() != null) {
            entity.setSchedules(new ArrayList<>(dto.schedules()));
        }
    }

    /**
     * Updates an existing Movie JPA entity from a MovieDto payload, preserving database identity and audit fields.
     *
     * @param dto the source DTO
     * @param entity the target JPA entity to update
     */
    public void updateEntityFromDto(MovieDto dto, Movie entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setTitle(dto.title());
        entity.setDirector(dto.director());
        if (dto.duration() != null && !dto.duration().isBlank()) {
            entity.setDuration(dto.duration());
        }
        entity.setReleaseYear(dto.releaseYear());
        entity.setRating(dto.rating());
        entity.setSynopsis(dto.synopsis());

        if (dto.audience() != null && !dto.audience().isBlank()) {
            if (entity.getAudience() != null) {
                entity.getAudience().setName(dto.audience().trim());
            } else {
                entity.setAudience(new Audience(dto.audience().trim()));
            }
        }

        if (dto.genres() != null && !dto.genres().isEmpty()) {
            Set<Genre> newGenres = dto.genres().stream()
                .filter(g -> g != null && !g.isBlank())
                .map(g -> new Genre(g.trim()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
            entity.setGenres(newGenres);
        } else if (dto.genre() != null && !dto.genre().isBlank()) {
            entity.setGenre(dto.genre().trim());
        }

        if (dto.schedules() != null) {
            entity.setSchedules(new ArrayList<>(dto.schedules()));
        }
    }

    private List<String> extractGenreNames(Movie entity) {
        return (entity.getGenres() != null && !entity.getGenres().isEmpty())
            ? entity.getGenres().stream()
                .filter(Objects::nonNull)
                .map(Genre::getName)
                .filter(Objects::nonNull)
                .toList()
            : Collections.emptyList();
    }

    private Set<Genre> buildGenres(List<String> genreNames, String singleGenre) {
        Set<Genre> genres = new LinkedHashSet<>();
        if (genreNames != null && !genreNames.isEmpty()) {
            for (String g : genreNames) {
                if (g != null && !g.isBlank()) {
                    genres.add(new Genre(g.trim()));
                }
            }
        } else if (singleGenre != null && !singleGenre.isBlank()) {
            for (String g : singleGenre.split(",")) {
                if (!g.trim().isBlank()) {
                    genres.add(new Genre(g.trim()));
                }
            }
        }
        return genres;
    }
}
