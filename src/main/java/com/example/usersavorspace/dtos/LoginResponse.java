package com.example.usersavorspace.dtos;

import com.example.usersavorspace.entities.User;

public class LoginResponse {
    private String token;
    private String refreshToken;
    private long expiresIn;
    private Integer userId;
    private User user;
    private String error;
    private String status;

    public String getToken() {
        return token;
    }

    public LoginResponse setToken(String token) {
        this.token = token;
        return this;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public LoginResponse setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public LoginResponse setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    public Integer getUserId() {
        return userId;
    }

    public LoginResponse setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public User getUser() {
        return user;
    }

    public LoginResponse setUser(User user) {
        this.user = user;
        return this;
    }

    public String getError() {
        return error;
    }

    public LoginResponse setError(String error) {
        this.error = error;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public LoginResponse setStatus(String status) {
        this.status = status;
        return this;
    }
}