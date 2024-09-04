package com.project.movies.service;

import java.util.Optional;

import com.project.movies.model.User;
import com.project.movies.repository.UserRepository;
import com.project.movies.repository.ViewingHistoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ViewingHistoryRepository viewingHistoryRepository;

    // 重複するユーザーIDの確認 メソッド
    public boolean isUserIdExists(String userId) {
        return userRepository.findByUserid(userId).isPresent();
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findUserByUserid(String userid) {
        return userRepository.findByUserid(userid);
    }

    public boolean validateUserLogin(String userid, String pwd) {
        Optional<User> user = userRepository.findByUserid(userid);
        return user.isPresent() && user.get().getPwd().equals(pwd);
    }

    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public User updateUser(User user) {
        // useridを使用してデータベースから既存のユーザー情報を取得
        Optional<User> userOptional = userRepository.findById(Long.valueOf(user.getUserid()));
        // ユーザーが存在する場合は情報を更新
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            // パスワードと名前を新しいデータに更新
            existingUser.setPwd(user.getPwd());
            existingUser.setName(user.getName());
            // 更新されたユーザー情報を保存
            return userRepository.save(existingUser);
        } else {
            // ユーザーが見つからない場合はnull
            return null;
        }
    }

    @Transactional      // 全て成功または全て失敗
    public void deleteUser(Long userId) {
        // まずViewingHistoryデータを削除
        viewingHistoryRepository.deleteByUserId(userId);

        // 次にUserデータを削除
        userRepository.deleteById(userId);
    }

}
