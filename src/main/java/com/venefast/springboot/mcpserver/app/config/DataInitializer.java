package com.venefast.springboot.mcpserver.app.config;

import com.venefast.springboot.mcpserver.app.models.entities.Audience;
import com.venefast.springboot.mcpserver.app.models.entities.Genre;
import com.venefast.springboot.mcpserver.app.models.entities.Movie;
import com.venefast.springboot.mcpserver.app.repositories.AudienceRepository;
import com.venefast.springboot.mcpserver.app.repositories.GenreRepository;
import com.venefast.springboot.mcpserver.app.repositories.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CommandLineRunner component that populates the database with the normalized initial movie catalog
 * (Movies, Genres/Categories, Audiences, Showtimes) if the movies repository is empty.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final AudienceRepository audienceRepository;

    public DataInitializer(
            MovieRepository movieRepository,
            GenreRepository genreRepository,
            AudienceRepository audienceRepository
    ) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.audienceRepository = audienceRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (movieRepository.count() > 0) {
            log.info(">>> Movie database already contains records. Skipping seed data.");
            return;
        }

        log.info(">>> Seeding normalized initial movie catalog with genres, audiences, and schedules...");

        Map<String, Genre> genreCache = new HashMap<>();
        Map<String, Audience> audienceCache = new HashMap<>();

        createMovie(
            "Inception", "Christopher Nolan", "148 min", "4.8/5", 2010,
            "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
            "PG-13 - Parents Strongly Cautioned",
            List.of("Sci-Fi", "Action", "Thriller"),
            List.of("14:00", "17:15", "20:30", "23:15"),
            genreCache, audienceCache
        );

        createMovie(
            "The Godfather", "Francis Ford Coppola", "175 min", "4.9/5", 1972,
            "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.",
            "R - Restricted",
            List.of("Crime", "Drama"),
            List.of("15:00", "18:45", "22:15"),
            genreCache, audienceCache
        );

        createMovie(
            "Interstellar", "Christopher Nolan", "169 min", "4.7/5", 2014,
            "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.",
            "PG-13 - Parents Strongly Cautioned",
            List.of("Sci-Fi", "Adventure", "Drama"),
            List.of("13:30", "17:00", "20:30"),
            genreCache, audienceCache
        );

        createMovie(
            "Pulp Fiction", "Quentin Tarantino", "154 min", "4.8/5", 1994,
            "The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.",
            "R - Restricted",
            List.of("Crime", "Drama", "Black Comedy"),
            List.of("16:00", "19:30", "22:45"),
            genreCache, audienceCache
        );

        createMovie(
            "Spirited Away", "Hayao Miyazaki", "125 min", "4.7/5", 2001,
            "During her family's move to the suburbs, a sullen 10-year-old girl wanders into a world ruled by gods, witches and spirits.",
            "PG - Parental Guidance",
            List.of("Animation", "Fantasy", "Adventure"),
            List.of("12:00", "14:30", "17:00", "19:30"),
            genreCache, audienceCache
        );

        createMovie(
            "Parasite", "Bong Joon Ho", "132 min", "4.6/5", 2019,
            "Greed and class discrimination threaten the newly formed symbiotic relationship between the wealthy Park family and the destitute Kim clan.",
            "R - Restricted",
            List.of("Drama", "Thriller", "Comedy"),
            List.of("15:30", "18:15", "21:00"),
            genreCache, audienceCache
        );

        createMovie(
            "The Dark Knight", "Christopher Nolan", "152 min", "4.9/5", 2008,
            "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
            "PG-13 - Parents Strongly Cautioned",
            List.of("Action", "Crime", "Drama"),
            List.of("14:15", "17:30", "20:45", "23:45"),
            genreCache, audienceCache
        );

        createMovie(
            "Blade Runner 2049", "Denis Villeneuve", "164 min", "4.5/5", 2017,
            "Young Blade Runner K's discovery of a long-buried secret leads him to track down former Blade Runner Rick Deckard, who's been missing for thirty years.",
            "R - Restricted",
            List.of("Sci-Fi", "Mystery", "Drama"),
            List.of("16:30", "19:45", "22:30"),
            genreCache, audienceCache
        );

        createMovie(
            "Gladiator II", "Ridley Scott", "148 min", "4.6/5", 2024,
            "Years after witnessing the death of Maximus, Lucius must enter the Colosseum after his home is conquered by tyrannical emperors.",
            "R - Restricted",
            List.of("Action", "Historical Drama", "Adventure"),
            List.of("15:00", "18:15", "21:30"),
            genreCache, audienceCache
        );

        createMovie(
            "Dune: Part Two", "Denis Villeneuve", "166 min", "4.8/5", 2024,
            "Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family.",
            "PG-13 - Parents Strongly Cautioned",
            List.of("Sci-Fi", "Adventure", "Action"),
            List.of("13:00", "16:45", "20:15"),
            genreCache, audienceCache
        );

        log.info(">>> Movie database initialization completed successfully with 10 movies, categories, and screening schedules.");
    }

    /**
     * Creates and persists a Movie with full English metadata, resolving cached Genres and Audience.
     */
    private void createMovie(
            String title,
            String director,
            String duration,
            String rating,
            Integer releaseYear,
            String synopsis,
            String audienceName,
            List<String> genreNames,
            List<String> schedules,
            Map<String, Genre> genreCache,
            Map<String, Audience> audienceCache
    ) {
        Audience audience = audienceCache.computeIfAbsent(audienceName, name ->
            audienceRepository.findByName(name).orElseGet(() -> audienceRepository.save(new Audience(name)))
        );

        Set<Genre> genres = new LinkedHashSet<>();
        for (String gName : genreNames) {
            Genre genre = genreCache.computeIfAbsent(gName, name ->
                genreRepository.findByName(name).orElseGet(() -> genreRepository.save(new Genre(name)))
            );
            genres.add(genre);
        }

        Movie movie = new Movie(title, duration, rating, audience, genres, schedules);
        movie.setDirector(director);
        movie.setReleaseYear(releaseYear);
        movie.setSynopsis(synopsis);

        movieRepository.save(movie);
    }

    /**
     * Overloaded helper matching the exact seeder template pattern.
     */
    public void createMovie(
            String title,
            String duration,
            String rating,
            String audienceName,
            List<String> genreNames,
            List<String> schedules,
            Map<String, Genre> genreCache,
            Map<String, Audience> audienceCache
    ) {
        createMovie(
            title, "Unknown", duration, rating, 2024, "",
            audienceName, genreNames, schedules, genreCache, audienceCache
        );
    }
}
