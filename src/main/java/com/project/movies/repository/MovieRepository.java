package com.project.movies.repository;

import java.util.Optional;

import com.project.movies.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m WHERE LOWER(REPLACE(m.title, ' ', '')) LIKE LOWER(REPLACE(CONCAT('%', :title, '%'), ' ', ''))")
    Page<Movie> findByTitleContainingIgnoreCaseWithoutSpaces(@Param("title") String title, Pageable pageable);

    Optional<Movie> findByTmdbId(Long tmdbId);

}
