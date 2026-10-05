package com.venefast.springboot.mcpserver.app.services.impl;

import com.venefast.springboot.mcpserver.app.mappers.MovieMapper;
import com.venefast.springboot.mcpserver.app.models.dtos.MovieDto;
import com.venefast.springboot.mcpserver.app.models.entities.Movie;
import com.venefast.springboot.mcpserver.app.repositories.MovieRepository;
import com.venefast.springboot.mcpserver.app.services.MovieService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Service implementation for Movie business logic.
 * Manages transactional boundaries and uses MovieMapper for DTO/Entity transformations.
 */
@Service
@Transactional(readOnly = true)
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public MovieServiceImpl(MovieRepository movieRepository, MovieMapper movieMapper) {
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
    }

    @Override
    public List<MovieDto> findAll() {
        return movieRepository.findAll().stream()
            .map(movieMapper::toDto)
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
    @Transactional
    public MovieDto create(MovieDto movieDto) {
        Movie entity = movieMapper.toEntity(movieDto);
        // Ensure ID is managed by auto-increment on create
        entity.setId(null);
        Movie saved = movieRepository.save(entity);
        return movieMapper.toDto(saved);
    }

    @Override
    @Transactional
    public MovieDto update(Long id, MovieDto movieDto) {
        Movie existing = movieRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Movie not found with id: " + id));

        movieMapper.updateEntityFromDto(movieDto, existing);
        Movie updated = movieRepository.save(existing);
        return movieMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new NoSuchElementException("Movie not found with id: " + id);
        }
        movieRepository.deleteById(id);
    }
}
