package com.project.movies.repository;

import com.project.movies.model.ViewingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ViewingHistoryRepository extends JpaRepository<ViewingHistory, Long> {
    List<ViewingHistory> findByUserId(Long userId);  // 특정 사용자의 시청 기록 조회
}
