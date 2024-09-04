package com.project.movies.repository;

import com.project.movies.model.ViewingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ViewingHistoryRepository extends JpaRepository<ViewingHistory, Long> {

    List<ViewingHistory> findByUserId(Long userId);  // 特定ユーザーの視聴履歴取得

    // JPAに、このQueryがINSERT、UPDATE、またはDELETE操作を実行することを通知
    @Modifying
    @Query("DELETE FROM ViewingHistory vh WHERE vh.userId = :userId AND vh.movieId = :movieId")
    void deleteViewingHistory(@Param("userId") Long userId, @Param("movieId") Long movieId);

    void deleteByUserId(Long userId);

}
