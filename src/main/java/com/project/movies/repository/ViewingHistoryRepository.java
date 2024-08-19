package com.project.movies.repository;

import com.project.movies.model.ViewingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ViewingHistoryRepository extends JpaRepository<ViewingHistory, Long> {

    List<ViewingHistory> findByUserId(Long userId);  // 특정 사용자의 시청 기록 조회

    // JPA에게 이 쿼리가 INSERT, UPDATE, 또는 DELETE 작업을 수행할 것임을 알림.
    @Modifying
    @Query("DELETE FROM ViewingHistory vh WHERE vh.userId = :userId AND vh.movieId = :movieId")
    void deleteViewingHistory(@Param("userId") Long userId, @Param("movieId") Long movieId);

    void deleteByUserId(Long userId);

}
