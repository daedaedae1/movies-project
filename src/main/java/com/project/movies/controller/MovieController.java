package com.project.movies.controller;

import com.project.movies.model.Movie;
import com.project.movies.service.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true") // 필요한 경우 CORS 설정
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/get")
    public Page<Movie> getAllMoviesPageable(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return movieService.getAllMoviesPageable(page, size);
    }

    @GetMapping("/search")
    public Page<Movie> searchMovies(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return movieService.searchMovies(query, page, size);
    }

}
