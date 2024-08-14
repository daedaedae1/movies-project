package com.project.movies.seeder;

import java.util.List;
import java.util.Optional;

import com.project.movies.dto.MovieDto;
import com.project.movies.model.Movie;
import com.project.movies.repository.MovieRepository;
import com.project.movies.service.TmdbService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DbSeeder implements CommandLineRunner {

    private final MovieRepository movieRepository;

    private final TmdbService tmdbService;

    public DbSeeder(MovieRepository movieRepository, TmdbService tmdbService) {
        this.movieRepository = movieRepository;
        this.tmdbService = tmdbService;
    }

    @Override
    public void run(String... args) throws Exception {
        List<MovieDto> moviesData = tmdbService.fetchMovies();

        for (MovieDto movieDto : moviesData) {
            // TMDB ID를 사용해 기존에 같은 영화가 저장되어 있는지 확인.
            Optional<Movie> existingMovie = movieRepository.findByTmdbId(movieDto.getTmdbId());
            if (!existingMovie.isPresent()) { // 같은 TMDB ID를 가진 영화가 없는 경우에만 저장.
                Movie movie = new Movie();
                movie.setTmdbId(movieDto.getTmdbId()); // TMDB ID 설정
                movie.setTitle((movieDto.getTitle() != null && !movieDto.getTitle().isEmpty()) ? movieDto.getTitle() : "No title.");
                movie.setOverview((movieDto.getOverview() != null && !movieDto.getOverview().isEmpty()) ? movieDto.getOverview() : "No overivew.");
                movie.setPosterPath((movieDto.getPosterPath() != null && !movieDto.getPosterPath().isEmpty()) ? movieDto.getPosterPath() : "No poster.");

                movieRepository.save(movie);
            }
        }
    }
}
