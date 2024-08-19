package com.project.movies.service;

import com.project.movies.model.Movie;
import com.project.movies.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // 페이지네이션을 위한 메소드.
    public Page<Movie> getAllMoviesPageable(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findAll(pageable);
    }

    // 영화 검색 메소드: 제목 포함 검색. (대소문자 및 띄어쓰기 구분 없음)
    public Page<Movie> searchMovies(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findByTitleContainingIgnoreCaseWithoutSpaces(title, pageable);
    }

    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    // 컨텐츠 기반 추천 메소드.
    public List<Movie> getContentBasedRecommendations(Long userId) {
        // 사용자 시청 기록에서 장르 ID를 가져옵니다.
        List<Long> viewedMovieIds = movieRepository.findMoviesWatchedByUser(userId);
        if (viewedMovieIds.isEmpty()) {
            return List.of(); // 시청 기록이 없는 경우 빈 목록 반환
        }

        // 시청 기록에 기반하여 추천할 영화의 ID 목록을 가져옵니다.
        List<Long> recommendedMovieIds = movieRepository.findRecommendedMovieIds(userId);

        // 추천 영화 목록을 가져옵니다.
        return movieRepository.findAllById(recommendedMovieIds);
    }
}
