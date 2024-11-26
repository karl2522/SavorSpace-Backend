package com.example.usersavorspace.services;

import com.example.usersavorspace.entities.Recipe;
import com.example.usersavorspace.entities.User;
import com.example.usersavorspace.exceptions.RecipeNotFoundException;
import com.example.usersavorspace.exceptions.ResourceNotFoundException;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final Path fileStorageLocation;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository, UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        }catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Transactional
    public Recipe addRecipe(Recipe recipe, Integer userId, MultipartFile image) {
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

    public Recipe updateRecipe(int recipeId, Recipe recipe, Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Recipe existingRecipe = recipeRepository.findByRecipeIDAndUser(recipeId, user)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found with id: " + recipeId));

        existingRecipe.setTitle(recipe.getTitle());
        existingRecipe.setDescription(recipe.getDescription());
        existingRecipe.setIngredients(recipe.getIngredients());
        existingRecipe.setInstructions(recipe.getInstructions());
        existingRecipe.setImageURL(recipe.getImageURL());
        existingRecipe.setUpdatedAt(LocalDateTime.now());

        return recipeRepository.save(existingRecipe);
    }

    public void deleteRecipe(int recipeId, Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));


        if(!recipeRepository.existsByRecipeIDAndUser(recipeId, user)) {
            throw new RecipeNotFoundException("Recipe not found with id: " + recipeId);
        }

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
}
