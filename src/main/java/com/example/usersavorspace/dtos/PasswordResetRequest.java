package com.example.usersavorspace.dtos;

public class PasswordResetRequest {

    private String email;
    private String newPassword;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
