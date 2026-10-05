package com.venefast.springboot.mcpserver.app.mappers;

import com.venefast.springboot.mcpserver.app.models.dtos.MovieCatalogDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieCreateDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieScheduleDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieUpdateDto;
import com.venefast.springboot.mcpserver.app.models.entities.Audience;
import com.venefast.springboot.mcpserver.app.models.entities.Genre;
import com.venefast.springboot.mcpserver.app.models.entities.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    @DisplayName("Should correctly map normalized fields: Audience, Genres, Duration, and Schedules")
    void shouldMapNormalizedFields() {
        MovieDto dto = new MovieDto(
            1L,
            "Inception",
            "Christopher Nolan",
            "148 min",
            "Sci-Fi",
            List.of("Sci-Fi", "Action", "Thriller"),
            "PG-13",
            List.of("14:00", "17:15", "20:30"),
            2010,
            8.8,
            "Dream heist"
        );

        Movie entity = movieMapper.toEntity(dto);
        assertNotNull(entity);
        assertEquals("148 min", entity.getDuration());
        assertEquals("PG-13", entity.getAudience().getName());
        assertEquals(3, entity.getGenres().size());
        assertEquals(3, entity.getSchedules().size());

        MovieDto convertedDto = movieMapper.toDto(entity);
        assertNotNull(convertedDto);
        assertEquals("148 min", convertedDto.duration());
        assertEquals("PG-13", convertedDto.audience());
        assertEquals(3, convertedDto.genres().size());
        assertEquals(3, convertedDto.schedules().size());
    }

    @Test
    @DisplayName("Should correctly map Movie entity to MovieCatalogDto")
    void shouldMapEntityToCatalogDto() {
        Movie movie = new Movie("Inception", "148 min", "8.8", new Audience("PG-13"), Set.of(new Genre("Sci-Fi")), List.of("14:00"));
        movie.setId(1L);

        MovieCatalogDto catalogDto = movieMapper.toCatalogDto(movie);

        assertNotNull(catalogDto);
        assertEquals(1L, catalogDto.id());
        assertEquals("Inception", catalogDto.title());
        assertEquals("148 min", catalogDto.duration());
        assertEquals("PG-13", catalogDto.audience());
        assertEquals(8.8, catalogDto.rating());
    }

    @Test
    @DisplayName("Should correctly map Movie entity to MovieScheduleDto")
    void shouldMapEntityToScheduleDto() {
        Movie movie = new Movie("Inception", "148 min", "8.8", new Audience("PG-13"), Set.of(new Genre("Sci-Fi")), List.of("14:00", "17:15"));
        movie.setId(1L);

        MovieScheduleDto scheduleDto = movieMapper.toScheduleDto(movie);

        assertNotNull(scheduleDto);
        assertEquals("Inception", scheduleDto.title());
        assertEquals("148 min", scheduleDto.duration());
        assertEquals("PG-13", scheduleDto.audience());
        assertEquals(2, scheduleDto.schedules().size());
        assertTrue(scheduleDto.available());
    }

    @Test
    @DisplayName("Should map MovieCreateDto to Movie entity")
    void shouldMapCreateDtoToEntity() {
        MovieCreateDto createDto = new MovieCreateDto(
            "Interstellar",
            "Christopher Nolan",
            "169 min",
            "Sci-Fi",
            List.of("Sci-Fi", "Adventure"),
            "PG-13",
            List.of("15:00", "19:00"),
            2014,
            8.7,
            "Space odyssey"
        );

        Movie entity = movieMapper.toEntity(createDto);

        assertNotNull(entity);
        assertEquals("Interstellar", entity.getTitle());
        assertEquals("Christopher Nolan", entity.getDirector());
        assertEquals("169 min", entity.getDuration());
        assertEquals("PG-13", entity.getAudience().getName());
        assertEquals(2, entity.getGenres().size());
        assertEquals(2, entity.getSchedules().size());
    }

    @Test
    @DisplayName("Should update entity from MovieUpdateDto")
    void shouldUpdateEntityFromUpdateDto() {
        Movie movie = new Movie("Old Title", "100 min", "7.0", new Audience("TE"), Set.of(new Genre("Drama")), List.of("14:00"));
        movie.setId(3L);

        MovieUpdateDto updateDto = new MovieUpdateDto(
            "Updated Title",
            "Updated Director",
            "125 min",
            "Action",
            List.of("Action", "Thriller"),
            "+14 - Mayores",
            List.of("16:00", "20:00"),
            2025,
            9.2,
            "Updated plot"
        );

        movieMapper.updateEntityFromDto(updateDto, movie);

        assertEquals(3L, movie.getId());
        assertEquals("Updated Title", movie.getTitle());
        assertEquals("Updated Director", movie.getDirector());
        assertEquals("125 min", movie.getDuration());
        assertEquals("+14 - Mayores", movie.getAudience().getName());
        assertEquals(2, movie.getGenres().size());
        assertEquals(2, movie.getSchedules().size());
        assertEquals(9.2, movie.getRating());
    }
}
