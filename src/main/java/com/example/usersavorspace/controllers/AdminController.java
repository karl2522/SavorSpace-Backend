package com.example.usersavorspace.controllers;

import com.example.usersavorspace.dtos.CommentDTO;
import com.example.usersavorspace.dtos.UserStats;
import com.example.usersavorspace.entities.User;
import com.example.usersavorspace.repositories.CommentRepository;
import com.example.usersavorspace.repositories.RatingRepository;
import com.example.usersavorspace.repositories.RecipeRepository;
import com.example.usersavorspace.repositories.UserRepository;
import com.example.usersavorspace.services.CommentService;
import com.example.usersavorspace.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequestMapping("/admin")
@RestController
@CrossOrigin
public class AdminController {

    private final UserService userService;

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final RecipeRepository recipeRepository;
    private final RatingRepository ratingRepository;
    private final CommentService commentService;

    public AdminController(UserService userService, UserRepository userRepository, CommentRepository commentRepository, RecipeRepository recipeRepository, RatingRepository ratingRepository, CommentService commentService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.recipeRepository = recipeRepository;
        this.ratingRepository = ratingRepository;
        this.commentService = commentService;
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

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/dashboard-stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> data = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);

        // Get dates for the last 7 days
        List<String> dates = new ArrayList<>();
        List<Long> userCounts = new ArrayList<>();
        List<Long> recipeCounts = new ArrayList<>();
        List<Long> commentCounts = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDateTime startOfDay = now.minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = startOfDay.plusDays(1);

            dates.add(startOfDay.format(DateTimeFormatter.ofPattern("MMM dd")));
            userCounts.add(userRepository.countByCreatedAtBetween(startOfDay, endOfDay));
            recipeCounts.add(recipeRepository.countByCreatedAtBetween(startOfDay, endOfDay));
            commentCounts.add(commentRepository.countByCreatedAtBetween(startOfDay, endOfDay));
        }

        // Reverse lists to show oldest to newest
        Collections.reverse(dates);
        Collections.reverse(userCounts);
        Collections.reverse(recipeCounts);
        Collections.reverse(commentCounts);

        data.put("dates", dates);
        data.put("userCounts", userCounts);
        data.put("recipeCounts", recipeCounts);
        data.put("commentCounts", commentCounts);

        return ResponseEntity.ok(data);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/daily-activity")
    public ResponseEntity<Map<String, Object>> getDailyActivity() {
        Map<String, Object> data = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dayStart = now.minusHours(24);

        // Get hourly activity for last 24 hours
        List<String> timeLabels = new ArrayList<>();
        List<Long> recipeCreations = new ArrayList<>();
        List<Long> commentActivity = new ArrayList<>();
        List<Long> ratingActivity = new ArrayList<>();

        for (int i = 0; i < 24; i++) {
            LocalDateTime start = dayStart.plusHours(i);
            LocalDateTime end = start.plusHours(1);

            timeLabels.add(start.format(DateTimeFormatter.ofPattern("HH:mm")));

            recipeCreations.add(recipeRepository.countByCreatedAtBetween(start, end));
            commentActivity.add(commentRepository.countByCreatedAtBetween(start, end));
            ratingActivity.add(ratingRepository.countByCreatedAtBetween(start, end));
        }

        data.put("timeLabels", timeLabels);
        data.put("recipeCreations", recipeCreations);
        data.put("commentActivity", commentActivity);
        data.put("ratingActivity", ratingActivity);

        return ResponseEntity.ok(data);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/user-metrics")
    public ResponseEntity<Map<String, Object>> getUserMetrics() {
        Map<String, Object> data = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.minusMonths(1);

        // Get metrics for different aspects of user engagement
        long totalUsers = userRepository.count();
        long activeUsers = userRepository. countByActiveTrueAndDeletedFalse();
        long recipeCount = recipeRepository.count();
        long commentCount = commentRepository.count();
        long ratingCount = ratingRepository.count();
        double avgRating = ratingRepository.findAverageRating().orElse(0.0);

        data.put("metrics", Arrays.asList(
                totalUsers,
                activeUsers,
                recipeCount,
                commentCount,
                ratingCount,
                Math.round(avgRating * 100) / 100.0
        ));

        data.put("labels", Arrays.asList(
                "Total Users",
                "Active Users",
                "Total Recipes",
                "Total Comments",
                "Total Ratings",
                "Average Rating"
        ));

        return ResponseEntity.ok(data);
    }

    @GetMapping("/flagged-comments")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<CommentDTO>> getFlaggedComments() {
        List<CommentDTO> flaggedComments = commentService.getFlaggedComments();
        return ResponseEntity.ok(flaggedComments);
    }

    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteCommentByAdmin(commentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/recipes/{recipeId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Integer recipeId) {
        recipeRepository.deleteById(recipeId);
        return ResponseEntity.noContent().build();
    }
}