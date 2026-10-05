package com.venefast.springboot.mcpserver.app.mcp;

import com.venefast.springboot.mcpserver.app.models.dtos.MovieDto;
import com.venefast.springboot.mcpserver.app.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MovieMcpTools Unit Tests")
class MovieMcpToolsTest {

    @Mock
    private MovieService movieService;

    @InjectMocks
    private MovieMcpTools movieMcpTools;

    private MovieDto sampleMovie;

    @BeforeEach
    void setUp() {
        sampleMovie = new MovieDto(
            1L,
            "Inception",
            "Christopher Nolan",
            "Sci-Fi",
            2010,
            8.8,
            "Dream heist"
        );
    }

    @Test
    @DisplayName("searchMoviesByTitle MCP tool should return matching movies")
    void testSearchMoviesByTitle() {
        when(movieService.searchByTitle("Inception")).thenReturn(List.of(sampleMovie));

        List<MovieDto> result = movieMcpTools.searchMoviesByTitle("Inception");

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).title());
    }

    @Test
    @DisplayName("getMovieById MCP tool should return movie when found")
    void testGetMovieById() {
        when(movieService.findById(1L)).thenReturn(Optional.of(sampleMovie));

        MovieDto result = movieMcpTools.getMovieById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    @DisplayName("getMovieById MCP tool should throw exception when not found")
    void testGetMovieByIdNotFound() {
        when(movieService.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> movieMcpTools.getMovieById(99L));
    }

    @Test
    @DisplayName("getMoviesByGenre MCP tool should filter by genre")
    void testGetMoviesByGenre() {
        when(movieService.findByGenre("Sci-Fi")).thenReturn(List.of(sampleMovie));

        List<MovieDto> result = movieMcpTools.getMoviesByGenre("Sci-Fi");

        assertEquals(1, result.size());
        assertEquals("Sci-Fi", result.get(0).genre());
    }

    @Test
    @DisplayName("getTopRatedMovies MCP tool should return top rated movies")
    void testGetTopRatedMovies() {
        when(movieService.findTopRated(8.5)).thenReturn(List.of(sampleMovie));

        List<MovieDto> result = movieMcpTools.getTopRatedMovies(8.5);

        assertEquals(1, result.size());
        assertEquals(8.8, result.get(0).rating());
    }

    @Test
    @DisplayName("getAllMovies MCP tool should return all movies")
    void testGetAllMovies() {
        when(movieService.findAll()).thenReturn(List.of(sampleMovie));

        List<MovieDto> result = movieMcpTools.getAllMovies();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("addMovie MCP tool should create and return new movie")
    void testAddMovie() {
        MovieDto created = new MovieDto(2L, "Interstellar", "Christopher Nolan", "Sci-Fi", 2014, 8.7, "Space exploration");
        when(movieService.create(any(MovieDto.class))).thenReturn(created);

        MovieDto result = movieMcpTools.addMovie("Interstellar", "Christopher Nolan", "Sci-Fi", 2014, 8.7, "Space exploration");

        assertNotNull(result);
        assertEquals("Interstellar", result.title());
        assertEquals(2L, result.id());
    }

    @Test
    @DisplayName("deleteMovie MCP tool should invoke service delete")
    void testDeleteMovie() {
        String response = movieMcpTools.deleteMovie(1L);

        verify(movieService).delete(1L);
        assertTrue(response.contains("successfully deleted"));
    }

    @Test
    @DisplayName("getCatalogResource MCP resource should format catalog as text")
    void testGetCatalogResource() {
        when(movieService.findAll()).thenReturn(List.of(sampleMovie));

        String resource = movieMcpTools.getCatalogResource();

        assertTrue(resource.contains("[1] Inception (2010)"));
        assertTrue(resource.contains("Christopher Nolan"));
        assertTrue(resource.contains("Rating: 8.8/10"));
    }

    @Test
    @DisplayName("movieRecommendationPrompt MCP prompt should generate prompt string")
    void testMovieRecommendationPrompt() {
        when(movieService.findByGenre("Sci-Fi")).thenReturn(List.of(sampleMovie));

        String prompt = movieMcpTools.movieRecommendationPrompt("Sci-Fi");

        assertTrue(prompt.contains("expert film critic"));
        assertTrue(prompt.contains("Sci-Fi"));
        assertTrue(prompt.contains("Inception"));
    }
}
