package com.project.movies.service;

import com.project.movies.model.Movie;
import com.project.movies.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // 페이지네이션을 위한 메소드
    public Page<Movie> getAllMoviesPageable(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findAll(pageable);
    }

    // 영화 검색 메소드: 제목 포함 검색 (대소문자 및 띄어쓰기 구분 없음)
    public Page<Movie> searchMovies(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findByTitleContainingIgnoreCaseWithoutSpaces(title, pageable);
    }

    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    // 기타 필요한 메서드 추가 (예: findById, delete, etc.)
}
