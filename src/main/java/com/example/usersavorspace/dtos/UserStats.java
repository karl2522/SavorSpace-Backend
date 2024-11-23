package com.example.usersavorspace.dtos;

public class UserStats {

    private final int activeUsers;
    private final int inactiveUsers;

    public UserStats(int activeUsers, int inactiveUsers) {
        this.activeUsers = activeUsers;
        this.inactiveUsers = inactiveUsers;
    }

    public int getActiveUsers() {
        return activeUsers;
    }

    public int getInactiveUsers() {
        return inactiveUsers;
    }
}
