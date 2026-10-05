package com.venefast.springboot.mcpserver.app.mappers;

import com.venefast.springboot.mcpserver.app.models.dtos.MovieDto;
import com.venefast.springboot.mcpserver.app.models.entities.Movie;
import org.springframework.stereotype.Component;

/**
 * Dedicated mapper component for bidirectional conversions between Movie entity and MovieDto record.
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
        return new MovieDto(
            entity.getId(),
            entity.getTitle(),
            entity.getDirector(),
            entity.getGenre(),
            entity.getReleaseYear(),
            entity.getRating(),
            entity.getSynopsis()
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
        movie.setGenre(dto.genre());
        movie.setReleaseYear(dto.releaseYear());
        movie.setRating(dto.rating());
        movie.setSynopsis(dto.synopsis());
        return movie;
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
        entity.setGenre(dto.genre());
        entity.setReleaseYear(dto.releaseYear());
        entity.setRating(dto.rating());
        entity.setSynopsis(dto.synopsis());
    }
}
