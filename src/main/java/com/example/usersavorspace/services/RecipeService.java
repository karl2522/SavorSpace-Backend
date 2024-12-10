package com.example.usersavorspace.services;

import com.example.usersavorspace.entities.Recipe;
import com.example.usersavorspace.entities.User;
import com.example.usersavorspace.exceptions.RecipeNotFoundException;
import com.example.usersavorspace.exceptions.ResourceNotFoundException;
import com.example.usersavorspace.exceptions.UnauthorizedException;
import com.example.usersavorspace.repositories.MealPlanRepository;
import com.example.usersavorspace.repositories.RecipeRepository;
import com.example.usersavorspace.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final Path fileStorageLocation;
    private final MealPlanRepository mealPlanRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository, UserRepository userRepository, MealPlanRepository mealPlanRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.mealPlanRepository = mealPlanRepository;
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        }catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Transactional
    public Recipe addRecipe(Recipe recipe, Integer userId, MultipartFile image, MultipartFile video) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if(image != null && !image.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path targetLocation = this.fileStorageLocation.resolve(fileName);

            try {
                Files.copy(image.getInputStream(), targetLocation);
                recipe.setImageURL("/uploads/" + fileName);
            }catch (IOException ex) {
                throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
            }
        }

        if(video != null && !video.isEmpty()) {
            String videoFileName = System.currentTimeMillis() + "_video_" + video.getOriginalFilename();
            Path videoTargetLocation = this.fileStorageLocation.resolve(videoFileName);

            try {
                Files.copy(video.getInputStream(), videoTargetLocation);
                recipe.setVideoURL("/uploads/" + videoFileName);
            } catch (IOException ex) {
                throw new RuntimeException("Could not store video " + videoFileName + ". Please try again!", ex);
            }
        }

        recipe.setUser(user);
        recipe.setCreatedAt(LocalDateTime.now());
        return recipeRepository.save(recipe);
    }

    @Transactional()
    public Page<Recipe> getAllRecipes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return recipeRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Recipe getRecipeById(int recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found with id: " + recipeId));
    }

    public Recipe updateRecipe(int recipeId, Recipe recipe, Integer userId, MultipartFile video) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Recipe existingRecipe = recipeRepository.findByRecipeIDAndUser(recipeId, user)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found with id: " + recipeId));

        existingRecipe.setTitle(recipe.getTitle());
        existingRecipe.setDescription(recipe.getDescription());
        existingRecipe.setIngredients(recipe.getIngredients());
        existingRecipe.setInstructions(recipe.getInstructions());
        existingRecipe.setUpdatedAt(LocalDateTime.now());

        // Handle video update if provided
        if (video != null && !video.isEmpty()) {
            try {
                // Delete old video if exists
                if (existingRecipe.getVideoURL() != null) {
                    Path oldVideoPath = Paths.get("uploads", existingRecipe.getVideoURL().replaceFirst("/uploads/", ""));
                    Files.deleteIfExists(oldVideoPath);
                }

                // Save new video
                String fileName = UUID.randomUUID().toString() + "_" + video.getOriginalFilename();
                Path uploadPath = Paths.get("uploads");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(video.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                existingRecipe.setVideoURL("/uploads/" + fileName);
            } catch (IOException e) {
                throw new RuntimeException("Could not store the video file. Error: " + e.getMessage());
            }
        }

        return recipeRepository.save(existingRecipe);
    }

    public void deleteRecipe(int recipeId, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));

        if (!recipe.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to delete this recipe");
        }

        // Delete the video file if it exists
        if (recipe.getVideoURL() != null) {
            try {
                Path videoPath = Paths.get("uploads", recipe.getVideoURL().replaceFirst("/uploads/", ""));
                Files.deleteIfExists(videoPath);
            } catch (IOException e) {
            }
        }

        // Delete associated meal plans
        mealPlanRepository.deleteAllByRecipe_RecipeID(recipeId);

        // Delete the recipe
        recipeRepository.deleteById(recipeId);
    }

    @Transactional
    public void deleteRecipeByAdmin(int recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found with id: " + recipeId));
        recipeRepository.deleteById(recipeId);
    }

    public List<Recipe> getRelatedRecipes(int recipeId, int limit) {

        Recipe currentRecipe = getRecipeById(recipeId);

        return recipeRepository.findAll().stream()
                .filter(recipe -> recipe.getRecipeID() != recipeId)
                .filter(recipe -> {
                    boolean similarIngredients = recipe.getIngredients() != null &&
                            currentRecipe.getIngredients() != null &&
                            calculatesSimilarity(recipe.getIngredients(), currentRecipe.getIngredients()) > 0.3;

                    boolean similarTitle = recipe.getTitle() != null &&
                            currentRecipe.getTitle() != null &&
                            calculatesSimilarity(recipe.getTitle(), currentRecipe.getTitle()) > 0.3;

                    return similarIngredients || similarTitle;
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    private double calculatesSimilarity(String text1, String text2) {

        Set<String> words1 = new HashSet<>(Arrays.asList(text1.toLowerCase().split("\\W+")));
        Set<String> words2 = new HashSet<>(Arrays.asList(text2.toLowerCase().split("\\W+")));

        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    }

    public List<Recipe> getLatestUserRecipes(Integer userId, int limit) {
        return recipeRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, limit));
    }

    public List<Recipe> getPopularUserRecipes(Integer userId, int limit) {
        return recipeRepository.findMostCommentedRecipesByUser(userId, PageRequest.of(0, limit));
    }

    public List<Recipe> getRecipesByUserId(Integer userId) {
        return recipeRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public Page<Recipe> getAllVideoRecipes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return recipeRepository.findByVideoURLIsNotNullOrderByCreatedAtDesc(pageable);
    }

    @Transactional
    public Recipe addVideoRecipe(Recipe recipe, Integer userId, MultipartFile video, MultipartFile image) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validate video file
        if (video == null || video.isEmpty()) {
            throw new IllegalArgumentException("Video file is required for video recipes");
        }

        // Handle video upload
        String videoFileName = System.currentTimeMillis() + "_video_" + video.getOriginalFilename();
        Path videoTargetLocation = this.fileStorageLocation.resolve(videoFileName);

        try {
            Files.copy(video.getInputStream(), videoTargetLocation);
            recipe.setVideoURL("/uploads/" + videoFileName);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store video " + videoFileName + ". Please try again!", ex);
        }

        // Handle optional image upload
        if (image != null && !image.isEmpty()) {
            String imageFileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path imageTargetLocation = this.fileStorageLocation.resolve(imageFileName);

            try {
                Files.copy(image.getInputStream(), imageTargetLocation);
                recipe.setImageURL("/uploads/" + imageFileName);
            } catch (IOException ex) {
                throw new RuntimeException("Could not store image " + imageFileName + ". Please try again!", ex);
            }
        }

        recipe.setUser(user);
        recipe.setCreatedAt(LocalDateTime.now());
        return recipeRepository.save(recipe);
    }

    @Transactional
    public Page<Recipe> getAllImageRecipes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return recipeRepository.findByVideoURLIsNullOrderByCreatedAtDesc(pageable);
    }
}
