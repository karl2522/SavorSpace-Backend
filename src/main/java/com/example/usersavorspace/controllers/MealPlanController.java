package com.example.usersavorspace.controllers;

import com.example.usersavorspace.dtos.MealPlanDTO;
import com.example.usersavorspace.entities.User;
import com.example.usersavorspace.services.MealPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/meal-plans")
@CrossOrigin(origins = "http://localhost:5173")
public class MealPlanController {
    @Autowired
    private MealPlanService mealPlanService;

    @GetMapping("/month")
    public ResponseEntity<List<MealPlanDTO>> getMealPlansForMonth(
            @RequestParam int year,
            @RequestParam int month,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<MealPlanDTO> mealPlans = mealPlanService.getMealPlansForMonth(user.getId(), year, month);
        return ResponseEntity.ok(mealPlans);
    }

    @PostMapping
    public ResponseEntity<MealPlanDTO> addMealPlan(
            @RequestBody MealPlanDTO mealPlanDTO,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        MealPlanDTO savedPlan = mealPlanService.addMealPlan(user.getId(), mealPlanDTO);
        return ResponseEntity.ok(savedPlan);
    }

    @GetMapping
    public ResponseEntity<List<MealPlanDTO>> getMealPlans(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<MealPlanDTO> mealPlans = mealPlanService.getMealPlans(user.getId(), start, end);
        return ResponseEntity.ok(mealPlans);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMealPlan(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        mealPlanService.deleteMealPlan(id, user.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<MealPlanDTO> updateMealPlan(
            @PathVariable Long id,
            @RequestBody MealPlanDTO mealPlanDTO,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        MealPlanDTO updatedPlan = mealPlanService.updateMealPlan(id, user.getId(), mealPlanDTO);
        return ResponseEntity.ok(updatedPlan);
    }
}