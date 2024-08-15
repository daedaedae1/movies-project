package com.project.movies.service;
import com.project.movies.model.ViewingHistory;
import com.project.movies.repository.ViewingHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ViewingHistoryService {

    @Autowired
    private ViewingHistoryRepository viewingHistoryRepository;

    // 시청 기록 저장
    public ViewingHistory saveViewingHistory(ViewingHistory viewingHistory) {
        return viewingHistoryRepository.save(viewingHistory);
    }

    // 특정 사용자의 시청 기록 조회
    public List<ViewingHistory> getViewingHistoryByUserId(Long userId) {
        return viewingHistoryRepository.findByUserId(userId);
    }
}
