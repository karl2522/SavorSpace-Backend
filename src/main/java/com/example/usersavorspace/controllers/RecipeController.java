package com.example.usersavorspace.controllers;

import com.example.usersavorspace.entities.Recipe;
import com.example.usersavorspace.services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipes")
@CrossOrigin(origins = "http://localhost:5173")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    // Create
    @PostMapping
    public Recipe addRecipe(@RequestBody Recipe recipe) {
        return recipeService.addRecipe(recipe);
    }

    // Get all recipes
    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    // Get recipe by recipeID
    @GetMapping("/{recipeID}")
    public Recipe getRecipeById(@PathVariable int recipeID) {
        return recipeService.getRecipeById(recipeID);
    }

    // Update
    @PutMapping("/putRecipe")
    public Recipe updateRecipe(@RequestParam int recipeID, @RequestBody Recipe newRecipe) {
        return recipeService.updateRecipe(recipeID, newRecipe);
    }

    // Delete recipe by recipeID
    @DeleteMapping("/deleteRecipe/{recipeID}")
    public void deleteRecipe(@PathVariable int recipeID) {
        recipeService.deleteRecipe(recipeID);
    }

    //Delete all recipes
    @DeleteMapping("/deleteRecipe")
    public List<Recipe> deleteAllRecipes() {
        return recipeService.deleteAllRecipes();
    }

}
