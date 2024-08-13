package com.project.movies.service;

import java.util.Optional;

import com.project.movies.model.User;
import com.project.movies.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public User addUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findUserById(String userid) {
        return userRepository.findByUserid(userid);
    }

    public boolean validateUserLogin(String userid, String pwd) {
        Optional<User> user = userRepository.findByUserid(userid);
        return user.isPresent() && user.get().getPwd().equals(pwd); // Assumes passwords are stored in plain text (not recommended in production)
    }

    public User updateUser(User user) {
        // userid를 사용하여 데이터베이스에서 기존 사용자 정보 조회
        Optional<User> userOptional = userRepository.findByUserid(user.getUserid());

        // 해당 사용자가 존재하면 정보 업데이트
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            // 비밀번호와 이름을 새로운 값으로 업데이트
            existingUser.setPwd(user.getPwd());
            existingUser.setName(user.getName());
            // 업데이트된 사용자 정보 저장
            return userRepository.save(existingUser);
        } else {
            // 사용자를 찾을 수 없는 경우, null 리턴 (에러 처리나 다른 방식으로 처리 가능)
            return null;
        }
    }

    public void deleteUser(String userid) {
        Optional<User> user = userRepository.findByUserid(userid);
        userRepository.deleteById(user.get().getId());
    }

}
