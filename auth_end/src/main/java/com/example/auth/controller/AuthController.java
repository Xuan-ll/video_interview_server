package com.example.auth.controller;

import com.example.auth.model.ApiResponse;
import com.example.auth.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/user-info")
    public ApiResponse<User> getUserInfo(@RequestParam String token) {
        User user = new User(1L, "testuser", "Test User", "user");
        return ApiResponse.success(user);
    }
}