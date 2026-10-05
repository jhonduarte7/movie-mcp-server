package com.venefast.springboot.mcpserver.app.services.impl;

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
import com.venefast.springboot.mcpserver.app.services.MovieService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

/**
 * Service implementation for Movie business logic.
 * Manages transactional boundaries and uses MovieMapper, supporting modular DTO contracts.
 */
@Service
@Transactional(readOnly = true)
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final AudienceRepository audienceRepository;
    private final MovieMapper movieMapper;

    public MovieServiceImpl(
            MovieRepository movieRepository,
            GenreRepository genreRepository,
            AudienceRepository audienceRepository,
            MovieMapper movieMapper
    ) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.audienceRepository = audienceRepository;
        this.movieMapper = movieMapper;
    }

    @Override
    public List<MovieDto> findAll() {
        return movieRepository.findAll().stream()
            .map(movieMapper::toDto)
            .toList();
    }

    @Override
    public List<MovieCatalogDto> findCatalogMovies() {
        return movieRepository.findAll().stream()
            .map(movieMapper::toCatalogDto)
            .toList();
    }

    @Override
    public Optional<MovieDto> findById(Long id) {
        return movieRepository.findById(id)
            .map(movieMapper::toDto);
    }

    @Override
    public List<MovieDto> findByGenre(String genre) {
        return movieRepository.findByGenreIgnoreCase(genre).stream()
            .map(movieMapper::toDto)
            .toList();
    }

    @Override
    public List<MovieDto> searchByTitle(String query) {
        return movieRepository.findByTitleContainingIgnoreCase(query).stream()
            .map(movieMapper::toDto)
            .toList();
    }

    @Override
    public List<MovieDto> findByDirector(String director) {
        return movieRepository.findByDirectorContainingIgnoreCase(director).stream()
            .map(movieMapper::toDto)
            .toList();
    }

    @Override
    public List<MovieDto> findTopRated(Double minRating) {
        return movieRepository.findByRatingGreaterThanEqualOrderByRatingDesc(minRating).stream()
            .map(movieMapper::toDto)
            .toList();
    }

    @Override
    public List<MovieDto> findByAudience(String audience) {
        return movieRepository.findByAudienceContainingIgnoreCase(audience).stream()
            .map(movieMapper::toDto)
            .toList();
    }

    @Override
    public List<String> getMovieSchedules(Long id) {
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Movie not found with id: " + id));
        return (movie.getSchedules() != null) ? new ArrayList<>(movie.getSchedules()) : List.of();
    }

    @Override
    public MovieScheduleDto getMovieScheduleDetails(Long id) {
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Movie not found with id: " + id));
        return movieMapper.toScheduleDto(movie);
    }

    @Override
    public MovieScheduleDto getMovieScheduleDetailsByTitle(String title) {
        if (title == null || title.isBlank()) {
            return MovieScheduleDto.notFound("Unknown");
        }
        String cleanTitle = title.trim();
        Optional<Movie> movieOpt = movieRepository.findFirstByTitleIgnoreCase(cleanTitle);
        if (movieOpt.isEmpty()) {
            List<Movie> matches = movieRepository.findByTitleContainingIgnoreCase(cleanTitle);
            if (!matches.isEmpty()) {
                movieOpt = Optional.of(matches.get(0));
            }
        }

        return movieOpt.map(movieMapper::toScheduleDto)
            .orElseGet(() -> MovieScheduleDto.notFound(cleanTitle));
    }

    @Override
    public String getMovieScheduleByTitle(String title) {
        return getMovieScheduleDetailsByTitle(title).message();
    }

    @Override
    @Transactional
    public MovieDto create(MovieDto movieDto) {
        Movie entity = movieMapper.toEntity(movieDto);
        return persistNewMovie(entity);
    }

    @Override
    @Transactional
    public MovieDto create(MovieCreateDto createDto) {
        Movie entity = movieMapper.toEntity(createDto);
        return persistNewMovie(entity);
    }

    @Override
    @Transactional
    public MovieDto update(Long id, MovieDto movieDto) {
        Movie existing = movieRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Movie not found with id: " + id));

        movieMapper.updateEntityFromDto(movieDto, existing);
        return updateAndSaveMovie(existing);
    }

    @Override
    @Transactional
    public MovieDto update(Long id, MovieUpdateDto updateDto) {
        Movie existing = movieRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Movie not found with id: " + id));

        movieMapper.updateEntityFromDto(updateDto, existing);
        return updateAndSaveMovie(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new NoSuchElementException("Movie not found with id: " + id);
        }
        movieRepository.deleteById(id);
    }

    private MovieDto persistNewMovie(Movie entity) {
        entity.setId(null);

        // Resolve audience to avoid duplicates
        if (entity.getAudience() != null && entity.getAudience().getName() != null) {
            String audName = entity.getAudience().getName().trim();
            Audience audience = audienceRepository.findByName(audName)
                .orElseGet(() -> {
                    Audience saved = audienceRepository.save(new Audience(audName));
                    return (saved != null) ? saved : new Audience(audName);
                });
            entity.setAudience(audience);
        }

        // Resolve genres to avoid duplicates
        if (entity.getGenres() != null && !entity.getGenres().isEmpty()) {
            Set<Genre> resolved = new LinkedHashSet<>();
            for (Genre g : entity.getGenres()) {
                if (g != null && g.getName() != null && !g.getName().isBlank()) {
                    String gName = g.getName().trim();
                    Genre resolvedGenre = genreRepository.findByName(gName)
                        .orElseGet(() -> {
                            Genre saved = genreRepository.save(new Genre(gName));
                            return (saved != null) ? saved : new Genre(gName);
                        });
                    if (resolvedGenre != null) {
                        resolved.add(resolvedGenre);
                    }
                }
            }
            entity.setGenres(resolved);
        }

        Movie saved = movieRepository.save(entity);
        return movieMapper.toDto(saved);
    }

    private MovieDto updateAndSaveMovie(Movie existing) {
        if (existing.getAudience() != null && existing.getAudience().getName() != null) {
            String audName = existing.getAudience().getName().trim();
            Audience audience = audienceRepository.findByName(audName)
                .orElseGet(() -> {
                    Audience saved = audienceRepository.save(new Audience(audName));
                    return (saved != null) ? saved : new Audience(audName);
                });
            existing.setAudience(audience);
        }

        if (existing.getGenres() != null && !existing.getGenres().isEmpty()) {
            Set<Genre> resolved = new LinkedHashSet<>();
            for (Genre g : existing.getGenres()) {
                if (g != null && g.getName() != null && !g.getName().isBlank()) {
                    String gName = g.getName().trim();
                    Genre resolvedGenre = genreRepository.findByName(gName)
                        .orElseGet(() -> {
                            Genre saved = genreRepository.save(new Genre(gName));
                            return (saved != null) ? saved : new Genre(gName);
                        });
                    if (resolvedGenre != null) {
                        resolved.add(resolvedGenre);
                    }
                }
            }
            existing.setGenres(resolved);
        }

        Movie updated = movieRepository.save(existing);
        return movieMapper.toDto(updated);
    }
}
