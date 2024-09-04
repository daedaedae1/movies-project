package com.project.movies.service;
import com.project.movies.model.ViewingHistory;
import com.project.movies.repository.ViewingHistoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ViewingHistoryService {

    @Autowired
    private ViewingHistoryRepository viewingHistoryRepository;

    // 特定ユーザーの視聴履歴取得
    public List<ViewingHistory> getViewingHistoryByUserId(Long userId) {
        return viewingHistoryRepository.findByUserId(userId);
    }

    // 視聴履歴を保存
    public ViewingHistory saveViewingHistory(ViewingHistory viewingHistory) {
        return viewingHistoryRepository.save(viewingHistory);
    }

    // 視聴履歴を削除
    @Transactional
    public void deleteViewingHistory(Long userId, Long movieId) {
        viewingHistoryRepository.deleteViewingHistory(userId, movieId);
    }

}
