package com.example.auth.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
@Data
public class User {
    private Long id;
    private String username;

    @JsonProperty("nickName")
    private String nickName;
    private int role;

    // 构造函数
    public User(Long id, String username, String nickName, int role) {
        this.id = id;
        this.username = username;
        this.nickName = nickName;
        this.role = role;
    }

    public User() {
    }
}