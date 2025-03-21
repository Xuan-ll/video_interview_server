package com.example.auth.model;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String nickName;
    private int role;

    // 构造函数
    public User(Long id, String username, String nickName, String role) {
        this.id = id;
        this.username = username;
        this.nickName = nickName;int
        this.role = role;
    }

    public User() {
    }
}