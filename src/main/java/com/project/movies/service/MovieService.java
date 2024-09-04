package com.project.movies.service;

import com.project.movies.model.Movie;
import com.project.movies.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // ページネーションのためのメソッド
    public Page<Movie> getAllMoviesPageable(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findAll(pageable);
    }

    // 映画検索メソッド: タイトルを含む検索（大文字・小文字やスペースの区別なし）
    public Page<Movie> searchMovies(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findByTitleContainingIgnoreCaseWithoutSpaces(title, pageable);
    }

    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    // コンテンツベースの推薦メソッド
    public List<Movie> getContentBasedRecommendations(Long userId) {
        // ユーザーの視聴履歴からジャンルIDを取得
        List<Long> viewedMovieIds = movieRepository.findMoviesWatchedByUser(userId);
        if (viewedMovieIds.isEmpty()) {
            return List.of(); // 視聴履歴がない場合は空のリストを返す
        }

        // 視聴履歴に基づいて推薦する映画のIDリストを取得
        List<Long> recommendedMovieIds = movieRepository.findRecommendedMovieIds(userId);

        // 推薦映画のリストを取得
        return movieRepository.findAllById(recommendedMovieIds);
    }
}
