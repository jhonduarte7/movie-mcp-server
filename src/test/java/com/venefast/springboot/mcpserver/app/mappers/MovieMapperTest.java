package com.venefast.springboot.mcpserver.app.mappers;

import com.venefast.springboot.mcpserver.app.models.dtos.MovieDto;
import com.venefast.springboot.mcpserver.app.models.entities.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("MovieMapper Unit Tests")
class MovieMapperTest {

    private MovieMapper movieMapper;

    @BeforeEach
    void setUp() {
        movieMapper = new MovieMapper();
    }

    @Test
    @DisplayName("Should correctly map Movie entity to MovieDto")
    void shouldMapEntityToDto() {
        Movie movie = new Movie("Inception", "Christopher Nolan", "Sci-Fi", 2010, 8.8, "Dream inside dream");
        movie.setId(1L);

        MovieDto dto = movieMapper.toDto(movie);

        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Inception", dto.title());
        assertEquals("Christopher Nolan", dto.director());
        assertEquals("Sci-Fi", dto.genre());
        assertEquals(2010, dto.releaseYear());
        assertEquals(8.8, dto.rating());
        assertEquals("Dream inside dream", dto.synopsis());
    }

    @Test
    @DisplayName("Should return null when mapping null entity to DTO")
    void shouldReturnNullWhenEntityIsNull() {
        assertNull(movieMapper.toDto(null));
    }

    @Test
    @DisplayName("Should correctly map MovieDto to Movie entity")
    void shouldMapDtoToEntity() {
        MovieDto dto = new MovieDto(2L, "Interstellar", "Christopher Nolan", "Sci-Fi", 2014, 8.7, "Wormhole adventure");

        Movie entity = movieMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(2L, entity.getId());
        assertEquals("Interstellar", entity.getTitle());
        assertEquals("Christopher Nolan", entity.getDirector());
        assertEquals("Sci-Fi", entity.getGenre());
        assertEquals(2014, entity.getReleaseYear());
        assertEquals(8.7, entity.getRating());
        assertEquals("Wormhole adventure", entity.getSynopsis());
    }

    @Test
    @DisplayName("Should update entity fields from DTO preserving identity")
    void shouldUpdateEntityFromDto() {
        Movie movie = new Movie("Old Title", "Old Director", "Drama", 2000, 7.0, "Old Synopsis");
        movie.setId(5L);

        MovieDto updateDto = new MovieDto(null, "New Title", "New Director", "Action", 2024, 9.1, "New Synopsis");
        movieMapper.updateEntityFromDto(updateDto, movie);

        assertEquals(5L, movie.getId());
        assertEquals("New Title", movie.getTitle());
        assertEquals("New Director", movie.getDirector());
        assertEquals("Action", movie.getGenre());
        assertEquals(2024, movie.getReleaseYear());
        assertEquals(9.1, movie.getRating());
        assertEquals("New Synopsis", movie.getSynopsis());
    }
}
