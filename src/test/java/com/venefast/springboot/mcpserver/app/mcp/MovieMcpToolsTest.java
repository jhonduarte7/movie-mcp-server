package com.venefast.springboot.mcpserver.app.mcp;

import com.venefast.springboot.mcpserver.app.mcp.tools.AudienceTools;
import com.venefast.springboot.mcpserver.app.mcp.tools.GenreTools;
import com.venefast.springboot.mcpserver.app.mcp.tools.MovieRecommendationTools;
import com.venefast.springboot.mcpserver.app.mcp.tools.MovieTools;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieCatalogDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieCreateDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieScheduleDto;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieUpdateDto;
import com.venefast.springboot.mcpserver.app.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
@DisplayName("Modular Entity-Based MCP Tools Unit Tests")
class MovieMcpToolsTest {

    @Mock
    private MovieService movieService;

    private MovieTools movieTools;
    private GenreTools genreTools;
    private AudienceTools audienceTools;
    private MovieRecommendationTools recommendationTools;

    private MovieDto sampleMovie;

    @BeforeEach
    void setUp() {
        movieTools = new MovieTools(movieService);
        genreTools = new GenreTools(movieService);
        audienceTools = new AudienceTools(movieService);
        recommendationTools = new MovieRecommendationTools(movieService);

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

    // ==========================================
    // MovieTools Tests
    // ==========================================

    @Test
    @DisplayName("searchMoviesByTitle MCP tool should return matching movies")
    void testSearchMoviesByTitle() {
        when(movieService.searchByTitle("Inception")).thenReturn(List.of(sampleMovie));

        List<MovieDto> result = movieTools.searchMoviesByTitle("Inception");

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).title());
    }

    @Test
    @DisplayName("getMovieById MCP tool should return movie when found")
    void testGetMovieById() {
        when(movieService.findById(1L)).thenReturn(Optional.of(sampleMovie));

        MovieDto result = movieTools.getMovieById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    @DisplayName("getMovieById MCP tool should throw exception when not found")
    void testGetMovieByIdNotFound() {
        when(movieService.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> movieTools.getMovieById(99L));
    }

    @Test
    @DisplayName("getTopRatedMovies MCP tool should return top rated movies")
    void testGetTopRatedMovies() {
        when(movieService.findTopRated(8.5)).thenReturn(List.of(sampleMovie));

        List<MovieDto> result = movieTools.getTopRatedMovies(8.5);

        assertEquals(1, result.size());
        assertEquals(8.8, result.get(0).rating());
    }

    @Test
    @DisplayName("getAllMovies MCP tool should return all movies")
    void testGetAllMovies() {
        when(movieService.findAll()).thenReturn(List.of(sampleMovie));

        List<MovieDto> result = movieTools.getAllMovies();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getCatalogOverview MCP tool should return catalog overview DTOs")
    void testGetCatalogOverview() {
        MovieCatalogDto catalogDto = new MovieCatalogDto(1L, "Inception", "148 min", List.of("Sci-Fi"), "PG-13", 8.8);
        when(movieService.findCatalogMovies()).thenReturn(List.of(catalogDto));

        List<MovieCatalogDto> result = movieTools.getCatalogOverview();

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).title());
        verify(movieService).findCatalogMovies();
    }

    @Test
    @DisplayName("getMovieSchedules MCP tool should return showtimes for movie")
    void testGetMovieSchedules() {
        when(movieService.getMovieSchedules(1L)).thenReturn(List.of("14:00", "17:15", "20:30"));

        List<String> schedules = movieTools.getMovieSchedules(1L);

        assertEquals(3, schedules.size());
        assertTrue(schedules.contains("14:00"));
        verify(movieService).getMovieSchedules(1L);
    }

    @Test
    @DisplayName("getMovieScheduleDetails MCP tool should return MovieScheduleDto")
    void testGetMovieScheduleDetails() {
        MovieScheduleDto scheduleDto = MovieScheduleDto.of("Inception", "148 min", "PG-13", List.of("14:00", "17:15"));
        when(movieService.getMovieScheduleDetails(1L)).thenReturn(scheduleDto);

        MovieScheduleDto result = movieTools.getMovieScheduleDetails(1L);

        assertNotNull(result);
        assertEquals("Inception", result.title());
        assertEquals(2, result.schedules().size());
        verify(movieService).getMovieScheduleDetails(1L);
    }

    @Test
    @DisplayName("getMovieScheduleByTitle MCP tool should return schedules when movie exists")
    void testGetMovieScheduleByTitleFound() {
        when(movieService.getMovieScheduleByTitle("Inception"))
            .thenReturn("Screening schedules for 'Inception': 14:00, 17:15, 20:30");

        String result = movieTools.getMovieScheduleByTitle("Inception");

        assertTrue(result.contains("Inception"));
        assertTrue(result.contains("14:00"));
        verify(movieService).getMovieScheduleByTitle("Inception");
    }

    @Test
    @DisplayName("getMovieScheduleByTitle MCP tool should return clear non-existent message when movie not found")
    void testGetMovieScheduleByTitleNotFound() {
        when(movieService.getMovieScheduleByTitle("Avatar"))
            .thenReturn("The movie 'Avatar' does not exist in the catalog.");

        String result = movieTools.getMovieScheduleByTitle("Avatar");

        assertTrue(result.contains("does not exist in the catalog"));
        verify(movieService).getMovieScheduleByTitle("Avatar");
    }

    @Test
    @DisplayName("addMovie MCP tool should create and return new movie using MovieCreateDto")
    void testAddMovie() {
        MovieDto created = new MovieDto(2L, "Interstellar", "Christopher Nolan", "Sci-Fi", 2014, 8.7, "Space exploration");
        when(movieService.create(any(MovieCreateDto.class))).thenReturn(created);

        MovieDto result = movieTools.addMovie("Interstellar", "Christopher Nolan", "Sci-Fi", 2014, 8.7, "Space exploration");

        assertNotNull(result);
        assertEquals("Interstellar", result.title());
        assertEquals(2L, result.id());
        verify(movieService).create(any(MovieCreateDto.class));
    }

    @Test
    @DisplayName("updateMovie MCP tool should update and return modified movie using MovieUpdateDto")
    void testUpdateMovie() {
        MovieDto updated = new MovieDto(1L, "Inception - Remastered", "Christopher Nolan", "Sci-Fi", 2010, 9.0, "Updated synopsis");
        when(movieService.update(any(Long.class), any(MovieUpdateDto.class))).thenReturn(updated);

        MovieDto result = movieTools.updateMovie(1L, "Inception - Remastered", "Christopher Nolan", "Sci-Fi", 2010, 9.0, "Updated synopsis");

        assertNotNull(result);
        assertEquals("Inception - Remastered", result.title());
        assertEquals(9.0, result.rating());
        verify(movieService).update(any(Long.class), any(MovieUpdateDto.class));
    }

    @Test
    @DisplayName("deleteMovie MCP tool should invoke service delete")
    void testDeleteMovie() {
        String response = movieTools.deleteMovie(1L);

        verify(movieService).delete(1L);
        assertTrue(response.contains("successfully deleted"));
    }

    // ==========================================
    // GenreTools Tests
    // ==========================================

    @Test
    @DisplayName("getAllGenres MCP tool should return all genres")
    void testGetAllGenres() {
        when(movieService.findAllGenres()).thenReturn(List.of("Action", "Drama", "Sci-Fi"));

        List<String> genres = genreTools.getAllGenres();

        assertEquals(3, genres.size());
        assertEquals("Action", genres.get(0));
        verify(movieService).findAllGenres();
    }

    @Test
    @DisplayName("getMoviesByGenre MCP tool should filter by genre")
    void testGetMoviesByGenre() {
        when(movieService.findByGenre("Sci-Fi")).thenReturn(List.of(sampleMovie));

        List<MovieDto> result = genreTools.getMoviesByGenre("Sci-Fi");

        assertEquals(1, result.size());
        assertEquals("Sci-Fi", result.get(0).genre());
        verify(movieService).findByGenre("Sci-Fi");
    }

    // ==========================================
    // AudienceTools Tests
    // ==========================================

    @Test
    @DisplayName("getAllAudiences MCP tool should return all audience ratings")
    void testGetAllAudiences() {
        when(movieService.findAllAudiences()).thenReturn(List.of("PG-13", "R", "TE - Todo Espectador"));

        List<String> audiences = audienceTools.getAllAudiences();

        assertEquals(3, audiences.size());
        assertEquals("PG-13", audiences.get(0));
        verify(movieService).findAllAudiences();
    }

    @Test
    @DisplayName("getMoviesByAudience MCP tool should filter by audience")
    void testGetMoviesByAudience() {
        when(movieService.findByAudience("PG-13")).thenReturn(List.of(sampleMovie));

        List<MovieDto> result = audienceTools.getMoviesByAudience("PG-13");

        assertEquals(1, result.size());
        verify(movieService).findByAudience("PG-13");
    }

    // ==========================================
    // MovieRecommendationTools Tests
    // ==========================================

    @Test
    @DisplayName("getCatalogResource MCP resource should format catalog as text")
    void testGetCatalogResource() {
        when(movieService.findAll()).thenReturn(List.of(sampleMovie));

        String resource = recommendationTools.getCatalogResource();

        assertTrue(resource.contains("[1] Inception (2010)"));
        assertTrue(resource.contains("Christopher Nolan"));
        assertTrue(resource.contains("Rating: 8.8/10"));
    }

    @Test
    @DisplayName("movieRecommendationPrompt MCP prompt should generate prompt string")
    void testMovieRecommendationPrompt() {
        when(movieService.findByGenre("Sci-Fi")).thenReturn(List.of(sampleMovie));

        String prompt = recommendationTools.movieRecommendationPrompt("Sci-Fi");

        assertTrue(prompt.contains("expert film critic"));
        assertTrue(prompt.contains("Sci-Fi"));
        assertTrue(prompt.contains("Inception"));
    }
}
