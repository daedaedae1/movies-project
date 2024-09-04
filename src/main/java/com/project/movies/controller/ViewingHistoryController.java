    package com.project.movies.controller;
    import com.project.movies.model.ViewingHistory;
    import com.project.movies.service.ViewingHistoryService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
    @RequestMapping("/viewing_history")
    public class ViewingHistoryController {

        @Autowired
        private ViewingHistoryService viewingHistoryService;

        // 視聴履歴保存API
        @PostMapping
        public ResponseEntity<ViewingHistory> saveViewingHistory(@RequestBody ViewingHistory viewingHistory) {
            ViewingHistory savedHistory = viewingHistoryService.saveViewingHistory(viewingHistory);
            return ResponseEntity.ok(savedHistory);
        }

        // 視聴履歴削除API
        @DeleteMapping
        public ResponseEntity<Void> deleteViewingHistory(
                @RequestParam("userId") Long userId,
                @RequestParam("movieId") Long movieId) {
            viewingHistoryService.deleteViewingHistory(userId, movieId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // 特定ユーザーの視聴履歴取得API
        @GetMapping("/{userid}")
        public ResponseEntity<List<ViewingHistory>> getViewingHistory(@PathVariable("userid") Long userId) {
            List<ViewingHistory> viewingHistory = viewingHistoryService.getViewingHistoryByUserId(userId);
            return ResponseEntity.ok(viewingHistory);
        }

    }