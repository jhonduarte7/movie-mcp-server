package com.venefast.springboot.mcpserver.app.services;

import com.venefast.springboot.mcpserver.app.mappers.MovieMapper;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieDto;
import com.venefast.springboot.mcpserver.app.models.entities.Movie;
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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

    @Spy
    private MovieMapper movieMapper = new MovieMapper();

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie sampleEntity;

    @BeforeEach
    void setUp() {
        sampleEntity = new Movie("Inception", "Christopher Nolan", "Sci-Fi", 2010, 8.8, "Dream thief");
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
}
