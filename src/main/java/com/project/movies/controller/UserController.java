package com.project.movies.controller;

import java.util.Map;
import java.util.Optional;

import com.project.movies.model.User;
import com.project.movies.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    // 重複するユーザーIDの確認API
    @GetMapping("/checkUserId/{userid}")
    public ResponseEntity<?> checkUserId(@PathVariable("userid") String userid) {
        boolean exists = userService.isUserIdExists(userid);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        User savedUser = userService.addUser(user);
        return ResponseEntity.ok().body(Map.of("success", savedUser != null));
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpSession session) {
        String userid = credentials.get("userid");
        String pwd = credentials.get("pwd");

        Optional<User> user = userService.findUserByUserid(userid);
        boolean success = userService.validateUserLogin(userid, pwd);

        if (success && user.isPresent()) {
            session.setAttribute("LOGIN_USER_ID", userid);
            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "id", user.get().getId() // ログイン成功時、DBのIDを返す
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        // 現在のセッション無効化
        session.invalidate();
        return ResponseEntity.ok().body(Map.of("success", true));
    }

    @GetMapping("/details/{userid}")
    public ResponseEntity<?> getUserDetails(@PathVariable("userid") Long userId) {
        Optional<User> user = userService.findUserById(userId);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/details/{userid}/update")
    public ResponseEntity<?> update(@RequestBody User user) {
        // UserServiceを使用してユーザー情報を更新
        User updatedUser = userService.updateUser(user);
        // 更新されたユーザー情報がnullでなければ成功
        if (updatedUser != null) {
            return ResponseEntity.ok().body(Map.of("success", true, "updatedUser", updatedUser));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false,
                    "message", "User update failed."));
        }
    }

    @DeleteMapping("/details/{userid}/delete")
    public ResponseEntity<?> delete(@PathVariable("userid") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().body(Map.of("success", true));
    }

}
