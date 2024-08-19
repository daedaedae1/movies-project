package com.project.movies.model;
import jakarta.persistence.*;

@Entity
@Table(name = "viewing_history")
public class ViewingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;  // 사용자 ID

    @Column(name = "movie_id", nullable = false)
    private Long movieId;  // 영화 ID

    public ViewingHistory() {}  // 없으면 영화 리스트에서 (보기) 누르는 게 오류난다.

    public ViewingHistory(Long userId, Long movieId) {
        this.userId = userId;
        this.movieId = movieId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }
}
