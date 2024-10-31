package com.example.usersavorspace.services;

import com.example.usersavorspace.entities.Recipe;
import com.example.usersavorspace.entities.User;
import com.example.usersavorspace.exceptions.RecipeNotFoundException;
import com.example.usersavorspace.repositories.RecipeRepository;
import com.example.usersavorspace.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.NameNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    // Create recipe
    public Recipe addRecipe(Recipe recipe) {
        try {
            if (recipe.getUser() != null && recipe.getUser().getId() != null) {
                // Fetch the managed UserEntity from the database
                User user = userRepository.findById(recipe.getUser().getId())
                        .orElseThrow(() -> new NameNotFoundException("User not found with ID: " + recipe.getUser().getId()));

                // Set the managed UserEntity on the RecipeEntity
                recipe.setUser(user);
            }
            return recipeRepository.save(recipe);
        } catch (Exception e) {
            throw new RuntimeException("Recipe could not be added, please try again.", e);
        }
    }


    // Get all recipes
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    // Get recipe by recipeID
    public Recipe getRecipeById(int recipeID) {
        return recipeRepository.findById(recipeID)
                .orElseThrow(() -> new NoSuchElementException("Recipe not found with ID: " + recipeID));
    }


    // Update recipe
    public Recipe updateRecipe(int recipeID, Recipe recipe) {
        return recipeRepository.findById(recipeID)
                .map(existingRecipe -> {
                    existingRecipe.setTitle(recipe.getTitle());
                    existingRecipe.setDescription(recipe.getDescription());
                    existingRecipe.setIngredients(recipe.getIngredients());
                    existingRecipe.setInstructions(recipe.getInstructions());
                    existingRecipe.setImageUrl(recipe.getImageUrl());
                    return recipeRepository.save(existingRecipe);
                })
                .orElseThrow(() -> new RecipeNotFoundException("Recipe with ID " + recipeID + " not found!"));
    }


    // Delete recipe by recipeID
    public String deleteRecipe(int recipeID) {
        String msg;
        if (recipeRepository.findById(recipeID).isPresent()) {
            recipeRepository.deleteById(recipeID);
            msg = "Recipe record has been successfully deleted!";
        }
        else {
            msg = recipeID + " is NOT FOUND!";
        }
        return msg;
    }

    // Delete all recipes
    public List<Recipe> deleteAllRecipes() {
        List<Recipe> recipe = recipeRepository.findAll();
        if (!recipe.isEmpty()) {
            recipeRepository.deleteAll();
        }
        return recipe;
    }
}
