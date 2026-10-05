package com.venefast.springboot.mcpserver.app.services;

import com.venefast.springboot.mcpserver.app.mappers.MovieMapper;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieCatalogDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieCreateDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieScheduleDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieUpdateDto;
import com.venefast.springboot.mcpserver.app.models.entities.Audience;
import com.venefast.springboot.mcpserver.app.models.entities.Genre;
import com.venefast.springboot.mcpserver.app.models.entities.Movie;
import com.venefast.springboot.mcpserver.app.repositories.AudienceRepository;
import com.venefast.springboot.mcpserver.app.repositories.GenreRepository;
import com.venefast.springboot.mcpserver.app.repositories.MovieRepository;
import com.venefast.springboot.mcpserver.app.services.impl.MovieServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MovieService Unit Tests")
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private AudienceRepository audienceRepository;

    @Spy
    private MovieMapper movieMapper = new MovieMapper();

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie sampleEntity;

    @BeforeEach
    void setUp() {
        sampleEntity = new Movie(
            "Inception",
            "Christopher Nolan",
            "148 min",
            2010,
            8.8,
            "Dream thief",
            new Audience("PG-13"),
            new LinkedHashSet<>(Set.of(new Genre("Sci-Fi"))),
            List.of("14:00", "17:15", "20:30")
        );
        sampleEntity.setId(1L);
    }

    @Test
    @DisplayName("Should return all movies as DTOs")
    void shouldFindAllMovies() {
        when(movieRepository.findAll()).thenReturn(List.of(sampleEntity));

        List<MovieDto> result = movieService.findAll();

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).title());
        verify(movieRepository).findAll();
    }

    @Test
    @DisplayName("Should find movie by ID")
    void shouldFindMovieById() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));

        Optional<MovieDto> result = movieService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().id());
        assertEquals("Inception", result.get().title());
    }

    @Test
    @DisplayName("Should return empty optional when movie not found by ID")
    void shouldReturnEmptyWhenNotFound() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<MovieDto> result = movieService.findById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find movies by genre")
    void shouldFindByGenre() {
        when(movieRepository.findByGenreIgnoreCase("Sci-Fi")).thenReturn(List.of(sampleEntity));

        List<MovieDto> result = movieService.findByGenre("Sci-Fi");

        assertEquals(1, result.size());
        assertEquals("Sci-Fi", result.get(0).genre());
    }

    @Test
    @DisplayName("Should search movies by title")
    void shouldSearchByTitle() {
        when(movieRepository.findByTitleContainingIgnoreCase("cep")).thenReturn(List.of(sampleEntity));

        List<MovieDto> result = movieService.searchByTitle("cep");

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).title());
    }

    @Test
    @DisplayName("Should find top rated movies")
    void shouldFindTopRated() {
        when(movieRepository.findByRatingGreaterThanEqualOrderByRatingDesc(8.5)).thenReturn(List.of(sampleEntity));

        List<MovieDto> result = movieService.findTopRated(8.5);

        assertEquals(1, result.size());
        assertEquals(8.8, result.get(0).rating());
    }

    @Test
    @DisplayName("Should find movies by audience classification")
    void shouldFindByAudience() {
        when(movieRepository.findByAudienceContainingIgnoreCase("PG-13")).thenReturn(List.of(sampleEntity));

        List<MovieDto> result = movieService.findByAudience("PG-13");

        assertEquals(1, result.size());
        assertEquals("PG-13", result.get(0).audience());
    }

    @Test
    @DisplayName("Should retrieve movie schedules")
    void shouldGetMovieSchedules() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));

        List<String> schedules = movieService.getMovieSchedules(1L);

        assertEquals(3, schedules.size());
        assertTrue(schedules.contains("14:00"));
    }

    @Test
    @DisplayName("Should retrieve movie schedule by title ignoring case")
    void shouldGetMovieScheduleByTitle() {
        when(movieRepository.findFirstByTitleIgnoreCase("inception")).thenReturn(Optional.of(sampleEntity));

        String result = movieService.getMovieScheduleByTitle("inception");

        assertTrue(result.contains("Inception"));
        assertTrue(result.contains("14:00"));
        assertTrue(result.contains("17:15"));
    }

    @Test
    @DisplayName("Should return clear message when movie does not exist for schedule query")
    void shouldReturnNonExistentMessageWhenMovieNotFound() {
        when(movieRepository.findFirstByTitleIgnoreCase("Matrix")).thenReturn(Optional.empty());
        when(movieRepository.findByTitleContainingIgnoreCase("Matrix")).thenReturn(List.of());

        String result = movieService.getMovieScheduleByTitle("Matrix");

        assertTrue(result.contains("Matrix"));
        assertTrue(result.contains("does not exist in the catalog"));
    }

    @Test
    @DisplayName("Should create movie and return saved DTO")
    void shouldCreateMovie() {
        MovieDto inputDto = new MovieDto(null, "Tenet", "Christopher Nolan", "Sci-Fi", 2020, 7.3, "Time inversion");
        Movie savedEntity = new Movie("Tenet", "Christopher Nolan", "Sci-Fi", 2020, 7.3, "Time inversion");
        savedEntity.setId(10L);

        when(movieRepository.save(any(Movie.class))).thenReturn(savedEntity);

        MovieDto created = movieService.create(inputDto);

        assertNotNull(created);
        assertEquals(10L, created.id());
        assertEquals("Tenet", created.title());
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    @DisplayName("Should update existing movie")
    void shouldUpdateMovie() {
        MovieDto updateDto = new MovieDto(null, "Inception Re-release", "Christopher Nolan", "Sci-Fi", 2010, 9.0, "Updated synopsis");
        when(movieRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleEntity);

        MovieDto updated = movieService.update(1L, updateDto);

        assertNotNull(updated);
        assertEquals("Inception Re-release", updated.title());
        assertEquals(9.0, updated.rating());
    }

    @Test
    @DisplayName("Should throw exception when updating nonexistent movie")
    void shouldThrowWhenUpdatingNonexistent() {
        MovieDto updateDto = new MovieDto(null, "Test", "Director", "Drama", 2020, 8.0, "Synopsis");
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> movieService.update(999L, updateDto));
    }

    @Test
    @DisplayName("Should delete movie when exists")
    void shouldDeleteMovie() {
        when(movieRepository.existsById(1L)).thenReturn(true);

        movieService.delete(1L);

        verify(movieRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting nonexistent movie")
    void shouldThrowWhenDeletingNonexistent() {
        when(movieRepository.existsById(999L)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> movieService.delete(999L));
    }

    @Test
    @DisplayName("Should return catalog movies as MovieCatalogDto")
    void shouldFindCatalogMovies() {
        when(movieRepository.findAll()).thenReturn(List.of(sampleEntity));

        List<MovieCatalogDto> catalog = movieService.findCatalogMovies();

        assertEquals(1, catalog.size());
        assertEquals("Inception", catalog.get(0).title());
        assertEquals(8.8, catalog.get(0).rating());
    }

    @Test
    @DisplayName("Should return MovieScheduleDto by ID")
    void shouldGetMovieScheduleDetails() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));

        MovieScheduleDto schedule = movieService.getMovieScheduleDetails(1L);

        assertNotNull(schedule);
        assertEquals("Inception", schedule.title());
        assertEquals(3, schedule.schedules().size());
        assertTrue(schedule.available());
    }

    @Test
    @DisplayName("Should throw NoSuchElementException when movie ID not found for schedule details")
    void shouldGetMovieScheduleDetailsNotFound() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> movieService.getMovieScheduleDetails(99L));
    }

    @Test
    @DisplayName("Should return MovieScheduleDto by title")
    void shouldGetMovieScheduleDetailsByTitle() {
        when(movieRepository.findFirstByTitleIgnoreCase("inception")).thenReturn(Optional.of(sampleEntity));

        MovieScheduleDto schedule = movieService.getMovieScheduleDetailsByTitle("inception");

        assertNotNull(schedule);
        assertEquals("Inception", schedule.title());
        assertTrue(schedule.available());
    }

    @Test
    @DisplayName("Should return notFound MovieScheduleDto when movie title not found")
    void shouldReturnNotFoundScheduleDtoWhenMovieNotFoundByTitle() {
        when(movieRepository.findFirstByTitleIgnoreCase("Matrix")).thenReturn(Optional.empty());

        MovieScheduleDto schedule = movieService.getMovieScheduleDetailsByTitle("Matrix");

        assertNotNull(schedule);
        assertFalse(schedule.available());
        assertTrue(schedule.message().contains("does not exist"));
    }

    @Test
    @DisplayName("Should create movie from MovieCreateDto")
    void shouldCreateMovieFromCreateDto() {
        MovieCreateDto createDto = new MovieCreateDto(
            "Oppenheimer",
            "Christopher Nolan",
            "180 min",
            "Drama",
            List.of("Drama", "Biography"),
            "+18 - Adultos",
            List.of("15:00", "19:00"),
            2023,
            8.9,
            "Story of atomic bomb"
        );

        Movie saved = new Movie("Oppenheimer", "Christopher Nolan", "180 min", 2023, 8.9, "Story of atomic bomb", null, Set.of(), List.of());
        saved.setId(20L);
        when(movieRepository.save(any(Movie.class))).thenReturn(saved);

        MovieDto result = movieService.create(createDto);

        assertNotNull(result);
        assertEquals(20L, result.id());
        assertEquals("Oppenheimer", result.title());
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    @DisplayName("Should update movie from MovieUpdateDto")
    void shouldUpdateMovieFromUpdateDto() {
        MovieUpdateDto updateDto = new MovieUpdateDto(
            "Inception Definitive",
            "Christopher Nolan",
            "150 min",
            "Sci-Fi",
            List.of("Sci-Fi"),
            "PG-13",
            List.of("14:00", "20:00"),
            2010,
            9.1,
            "Updated synopsis"
        );

        when(movieRepository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(movieRepository.save(any(Movie.class))).thenReturn(sampleEntity);

        MovieDto result = movieService.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("Inception Definitive", result.title());
        assertEquals(9.1, result.rating());
        verify(movieRepository).save(sampleEntity);
    }
}
