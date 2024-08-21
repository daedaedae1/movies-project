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

@RestController		// rest와 그냥의 차이 알아두기
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    // 중복 아이디 확인 API
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
                    "id", user.get().getId() // 로그인 성공 시 사용자 ID를 반환
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        // 현재 세션을 무효화
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

    // /api/update
    @PostMapping("/details/{userid}/update")
    public ResponseEntity<?> update(@RequestBody User user) {
        // UserService를 사용하여 사용자 정보를 업데이트.
        User updatedUser = userService.updateUser(user);
        // 업데이트된 사용자 정보가 null이 아니라면 성공적으로 업데이트된 것으로 간주.
        if (updatedUser != null) {
            // 성공적으로 업데이트되었다는 응답을 반환.
            return ResponseEntity.ok().body(Map.of("success", true, "updatedUser", updatedUser));
        } else {
            // 업데이트에 실패했다면, 클라이언트에게 적절한 에러 메시지를 반환.
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User update failed."));
        }
    }

    @DeleteMapping("/details/{userid}/delete")
    public ResponseEntity<?> delete(@PathVariable("userid") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().body(Map.of("success", true));
    }

}
