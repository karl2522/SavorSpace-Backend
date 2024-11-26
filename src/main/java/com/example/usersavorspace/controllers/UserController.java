package com.example.usersavorspace.controllers;

import com.example.usersavorspace.dtos.PasswordChangeDTO;
import com.example.usersavorspace.entities.User;
import com.example.usersavorspace.repositories.UserRepository;
import com.example.usersavorspace.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequestMapping("/users")
@RestController
@CrossOrigin
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public UserController(
            UserService userService,
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

   /* @PostMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateAccount(
            @PathVariable Integer id,
            @RequestBody Map<String, String> credentials) {
        try {
            User user = userService.deactivateAccount(id, credentials.get("password"));
            return ResponseEntity.ok().build();
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/{id}/reactivate")
    public ResponseEntity<?> reactivateAccount(
            @PathVariable Integer id,
            @RequestBody Map<String, String> credentials) {
        try {
            User user = userService.reactivateAccount(id, credentials.get("password"));
            return ResponseEntity.ok().build();
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }*/

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody PasswordChangeDTO passwordChangeDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmailAndDeletedFalse(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(passwordChangeDTO.getOldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok()
                .body("Password changed successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> allUsers() {
        List<User> users = userService.allUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Integer id,
            @RequestPart(required = false) User user,
            @RequestPart(required = false)MultipartFile profilePic) {
        try {
            User updatedUser = userService.updateUser(id, user, profilePic);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    @GetMapping("/{userId}/stats")
    public ResponseEntity<Map<String, Long>> getUserStats(@PathVariable Integer userId) {
        try {
            long recipeCount = userService.getUserRecipeCount(userId);
            long commentCount = userService.getUserCommentCount(userId);
            long ratingCount = userService.getUserRatingCount(userId);

            Map<String, Long> stats = Map.of(
                    "recipeCount", recipeCount,
                    "commentCount", commentCount,
                    "ratingCount", ratingCount
            );

            return ResponseEntity.ok(stats);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}