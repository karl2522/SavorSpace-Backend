package com.example.usersavorspace.controllers;

import com.example.usersavorspace.dtos.UserStats;
import com.example.usersavorspace.entities.User;
import com.example.usersavorspace.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin")
@RestController
@CrossOrigin
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        User updatedUser = userService.adminUpdateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<User>> allUsers() {
        List<User> activeUsers = userService.findAllActiveUsers();
        return ResponseEntity.ok(activeUsers);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/ad")
    public ResponseEntity<User> authenticatedAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentAdmin = (User) authentication.getPrincipal();
        return ResponseEntity.ok(currentAdmin);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/deleted")
    public ResponseEntity<List<User>> allDeletedUsers() {
        List<User> deletedUsers = userService.findAllDeletedUsers();
        return ResponseEntity.ok(deletedUsers);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/users/{id}/restore")
    public ResponseEntity<User> restoreUser(@PathVariable Integer id) {
        User restoredUser = userService.restoreUser(id);
        return ResponseEntity.ok(restoredUser);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/stats")
    public ResponseEntity<UserStats> getUserStats() {
        int activeCount = userService.findAllActiveUsers().size();
        int deleteCount = userService.findAllDeletedUsers().size();
        return ResponseEntity.ok(new UserStats(activeCount, deleteCount));
    }
}