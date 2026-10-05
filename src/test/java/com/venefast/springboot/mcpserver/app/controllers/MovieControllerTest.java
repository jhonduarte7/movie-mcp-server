package com.venefast.springboot.mcpserver.app.controllers;

import com.venefast.springboot.mcpserver.app.models.dtos.MovieDto;
import com.venefast.springboot.mcpserver.app.services.MovieService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
@DisplayName("MovieController REST API Tests")
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @Test
    @DisplayName("GET /api/movies should return list of movies")
    void shouldReturnAllMovies() throws Exception {
        MovieDto movie = new MovieDto(1L, "Inception", "Christopher Nolan", "Sci-Fi", 2010, 8.8, "Dream theft");
        when(movieService.findAll()).thenReturn(List.of(movie));

        mockMvc.perform(get("/api/movies"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].title").value("Inception"));
    }

    @Test
    @DisplayName("GET /api/movies/{id} should return 200 when found")
    void shouldReturnMovieById() throws Exception {
        MovieDto movie = new MovieDto(1L, "Inception", "Christopher Nolan", "Sci-Fi", 2010, 8.8, "Dream theft");
        when(movieService.findById(1L)).thenReturn(Optional.of(movie));

        mockMvc.perform(get("/api/movies/{id}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    @DisplayName("GET /api/movies/{id} should return 404 when not found")
    void shouldReturn404WhenNotFound() throws Exception {
        when(movieService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/movies/{id}", 99L))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/movies/search should filter movies by title")
    void shouldSearchMoviesByTitle() throws Exception {
        MovieDto movie = new MovieDto(1L, "Inception", "Christopher Nolan", "Sci-Fi", 2010, 8.8, "Dream theft");
        when(movieService.searchByTitle("Incept")).thenReturn(List.of(movie));

        mockMvc.perform(get("/api/movies/search").param("title", "Incept"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("Inception"));
    }

    @Test
    @DisplayName("GET /api/movies/genre/{genre} should filter by genre")
    void shouldFilterByGenre() throws Exception {
        MovieDto movie = new MovieDto(1L, "Inception", "Christopher Nolan", "Sci-Fi", 2010, 8.8, "Dream theft");
        when(movieService.findByGenre("Sci-Fi")).thenReturn(List.of(movie));

        mockMvc.perform(get("/api/movies/genre/{genre}", "Sci-Fi"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].genre").value("Sci-Fi"));
    }

    @Test
    @DisplayName("POST /api/movies should create a new movie and return 201")
    void shouldCreateMovie() throws Exception {
        MovieDto createdDto = new MovieDto(12L, "Dune: Part Two", "Denis Villeneuve", "Sci-Fi", 2024, 8.6, "Paul Atreides unites with Chani");

        when(movieService.create(any(MovieDto.class))).thenReturn(createdDto);

        String jsonPayload = """
            {
                "title": "Dune: Part Two",
                "director": "Denis Villeneuve",
                "genre": "Sci-Fi",
                "releaseYear": 2024,
                "rating": 8.6,
                "synopsis": "Paul Atreides unites with Chani"
            }
            """;

        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(12))
            .andExpect(jsonPath("$.title").value("Dune: Part Two"));
    }

    @Test
    @DisplayName("POST /api/movies should return 400 Bad Request when validation fails")
    void shouldRejectInvalidMovieCreation() throws Exception {
        String invalidPayload = """
            {
                "title": "",
                "director": "",
                "genre": "",
                "releaseYear": 1800,
                "rating": 15.0,
                "synopsis": "Invalid movie"
            }
            """;

        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/movies/{id} should update and return 200")
    void shouldUpdateMovie() throws Exception {
        MovieDto updateDto = new MovieDto(1L, "Inception Extended", "Christopher Nolan", "Sci-Fi", 2010, 9.0, "Extended cut");
        when(movieService.update(eq(1L), any(MovieDto.class))).thenReturn(updateDto);

        String jsonPayload = """
            {
                "title": "Inception Extended",
                "director": "Christopher Nolan",
                "genre": "Sci-Fi",
                "releaseYear": 2010,
                "rating": 9.0,
                "synopsis": "Extended cut"
            }
            """;

        mockMvc.perform(put("/api/movies/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Inception Extended"))
            .andExpect(jsonPath("$.rating").value(9.0));
    }

    @Test
    @DisplayName("DELETE /api/movies/{id} should return 204 No Content")
    void shouldDeleteMovie() throws Exception {
        doNothing().when(movieService).delete(1L);

        mockMvc.perform(delete("/api/movies/{id}", 1L))
            .andExpect(status().isNoContent());
    }
}
