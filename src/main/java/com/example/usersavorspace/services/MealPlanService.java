package com.example.usersavorspace.services;

import com.example.usersavorspace.dtos.MealPlanDTO;
import com.example.usersavorspace.entities.MealPlan;
import com.example.usersavorspace.entities.Recipe;
import com.example.usersavorspace.entities.User;
import com.example.usersavorspace.repositories.MealPlanRepository;
import com.example.usersavorspace.repositories.RecipeRepository;
import com.example.usersavorspace.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MealPlanService {
    @Autowired
    private MealPlanRepository mealPlanRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RecipeRepository recipeRepository;

    public List<MealPlanDTO> getMealPlansForMonth(Integer userId, int year, int month) {
        return mealPlanRepository.findByUserIdAndYearAndMonth(userId, year, month)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Add this method
    public List<MealPlanDTO> getMealPlans(Integer userId, LocalDateTime start, LocalDateTime end) {
        return mealPlanRepository.findByUserId(userId)
                .stream()
                .filter(mealPlan -> {
                    LocalDateTime planDate = LocalDateTime.of(
                            mealPlan.getYear(),
                            mealPlan.getMonth(),
                            mealPlan.getDay(),
                            0, 0);
                    return !planDate.isBefore(start.toLocalDate().atStartOfDay()) &&
                            !planDate.isAfter(end.toLocalDate().atTime(23, 59, 59));
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MealPlanDTO addMealPlan(Integer userId, MealPlanDTO mealPlanDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Recipe recipe = recipeRepository.findById(mealPlanDTO.getRecipeId())
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        MealPlan mealPlan = new MealPlan();
        mealPlan.setUser(user);
        mealPlan.setRecipe(recipe);
        mealPlan.setYear(mealPlanDTO.getYear());
        mealPlan.setMonth(mealPlanDTO.getMonth());
        mealPlan.setDay(mealPlanDTO.getDay());
        mealPlan.setMealType(mealPlanDTO.getMealType());
        mealPlan.setNotes(mealPlanDTO.getNotes());

        MealPlan savedMealPlan = mealPlanRepository.save(mealPlan);
        return convertToDTO(savedMealPlan);
    }

    private MealPlanDTO convertToDTO(MealPlan mealPlan) {
        MealPlanDTO dto = new MealPlanDTO();
        dto.setId(mealPlan.getId());
        dto.setUserId(mealPlan.getUser().getId());
        dto.setRecipeId(mealPlan.getRecipe().getRecipeID());
        dto.setRecipeName(mealPlan.getRecipe().getTitle());
        dto.setRecipeImage(mealPlan.getRecipe().getImageURL());
        dto.setYear(mealPlan.getYear());
        dto.setMonth(mealPlan.getMonth());
        dto.setDay(mealPlan.getDay());
        dto.setMealType(mealPlan.getMealType());
        dto.setNotes(mealPlan.getNotes());
        dto.setCreatedAt(mealPlan.getCreatedAt());
        dto.setUpdatedAt(mealPlan.getUpdatedAt());
        return dto;
    }

    public void deleteMealPlan(Long id, Integer userId) {
        MealPlan mealPlan = mealPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal plan not found"));

        if (!mealPlan.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this meal plan");
        }

        mealPlanRepository.delete(mealPlan);
    }

    public MealPlanDTO updateMealPlan(Long id, Integer userId, MealPlanDTO mealPlanDTO) {
        MealPlan existingPlan = mealPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal plan not found"));

        if (!existingPlan.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to update this meal plan");
        }

        Recipe recipe = recipeRepository.findById(mealPlanDTO.getRecipeId())
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        existingPlan.setRecipe(recipe);
        existingPlan.setYear(mealPlanDTO.getYear());
        existingPlan.setMonth(mealPlanDTO.getMonth());
        existingPlan.setDay(mealPlanDTO.getDay());
        existingPlan.setMealType(mealPlanDTO.getMealType());
        existingPlan.setNotes(mealPlanDTO.getNotes());
        existingPlan.setUpdatedAt(LocalDateTime.now());

        MealPlan updatedPlan = mealPlanRepository.save(existingPlan);
        return convertToDTO(updatedPlan);
    }
}