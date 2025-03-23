package com.example.auth.model;

import lombok.Data;
@Data
public class User {
    private Long id;
    private String username;
    private String nickname;
    private int role;

    // 构造函数
    public User(Long id, String username, String nickName, int role) {
        this.id = id;
        this.username = username;
        this.nickname = nickName;
        this.role = role;
    }

    public User() {
    }
}