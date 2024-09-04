package com.project.movies.repository;

import java.util.List;
import java.util.Optional;

import com.project.movies.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m WHERE LOWER(REPLACE(m.title, ' ', '')) " +
            "LIKE LOWER(REPLACE(CONCAT('%', :title, '%'), ' ', ''))")
    Page<Movie> findByTitleContainingIgnoreCaseWithoutSpaces(@Param("title") String title, Pageable pageable);

    Optional<Movie> findByTmdbId(Long tmdbId);

    @Query("SELECT m.id FROM Movie m " +
            "JOIN m.genres g " +
            "WHERE g.id IN (" +
            "   SELECT g.id FROM Genre g " +
            "   JOIN g.movies m2 " +
            "   JOIN ViewingHistory vh ON m2.id = vh.movieId " +
            "   WHERE vh.userId = :userId" +
            ") AND m.id NOT IN (" +
            "   SELECT vh.movieId FROM ViewingHistory vh WHERE vh.userId = :userId" +
            ")")
    List<Long> findRecommendedMovieIds(@Param("userId") Long userId);

    @Query("SELECT vh.movieId FROM ViewingHistory vh WHERE vh.userId = :userId")
    List<Long> findMoviesWatchedByUser(@Param("userId") Long userId);

}
