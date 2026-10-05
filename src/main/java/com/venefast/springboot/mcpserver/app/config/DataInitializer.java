package com.venefast.springboot.mcpserver.app.config;

import com.venefast.springboot.mcpserver.app.models.dtos.MovieDto;
import com.venefast.springboot.mcpserver.app.services.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Seeds initial movie catalog data into the database at application startup.
 */
@Configuration
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final MovieService movieService;

    public DataInitializer(MovieService movieService) {
        this.movieService = movieService;
    }

    @Override
    public void run(String... args) {
        if (!movieService.findAll().isEmpty()) {
            log.info("Movie database already contains records. Skipping seed data.");
            return;
        }

        log.info("Seeding initial movie catalog data for MCP Server...");

        List<MovieDto> initialMovies = List.of(
            new MovieDto(
                "Inception",
                "Christopher Nolan",
                "Sci-Fi",
                2010,
                8.8,
                "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O."
            ),
            new MovieDto(
                "The Godfather",
                "Francis Ford Coppola",
                "Crime",
                1972,
                9.2,
                "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son."
            ),
            new MovieDto(
                "Interstellar",
                "Christopher Nolan",
                "Sci-Fi",
                2014,
                8.7,
                "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival."
            ),
            new MovieDto(
                "Pulp Fiction",
                "Quentin Tarantino",
                "Crime",
                1994,
                8.9,
                "The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption."
            ),
            new MovieDto(
                "Spirited Away",
                "Hayao Miyazaki",
                "Animation",
                2001,
                8.6,
                "During her family's move to the suburbs, a sullen 10-year-old girl wanders into a world ruled by gods, witches and spirits."
            ),
            new MovieDto(
                "Parasite",
                "Bong Joon Ho",
                "Drama",
                2019,
                8.5,
                "Greed and class discrimination threaten the newly formed symbiotic relationship between the wealthy Park family and the destitute Kim clan."
            ),
            new MovieDto(
                "The Dark Knight",
                "Christopher Nolan",
                "Action",
                2008,
                9.0,
                "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice."
            ),
            new MovieDto(
                "Blade Runner 2049",
                "Denis Villeneuve",
                "Sci-Fi",
                2017,
                8.0,
                "Young Blade Runner K's discovery of a long-buried secret leads him to track down former Blade Runner Rick Deckard, who's been missing for thirty years."
            )
        );

        for (MovieDto movie : initialMovies) {
            movieService.create(movie);
        }

        log.info("Movie database initialized successfully with {} movies.", initialMovies.size());
    }
}
