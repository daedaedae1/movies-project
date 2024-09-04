package com.project.movies.model;
import jakarta.persistence.*;

@Entity
@Table(name = "viewing_history")
public class ViewingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;  // User ID

    @Column(name = "movie_id", nullable = false)
    private Long movieId;  // Movie ID

    public ViewingHistory() {}  // これがないと、JPAはエンティティのインスタンスを作成できず、エラーが発生

    public ViewingHistory(Long userId, Long movieId) {
        this.userId = userId;
        this.movieId = movieId;
    }

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
