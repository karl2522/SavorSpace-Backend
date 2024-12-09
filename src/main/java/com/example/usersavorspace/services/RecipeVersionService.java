package com.example.usersavorspace.services;

import com.example.usersavorspace.entities.Recipe;
import com.example.usersavorspace.entities.RecipeVersion;
import com.example.usersavorspace.entities.User;
import com.example.usersavorspace.exceptions.ResourceNotFoundException;
import com.example.usersavorspace.repositories.RecipeRepository;
import com.example.usersavorspace.repositories.RecipeVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeVersionService {
    @Autowired
    private RecipeVersionRepository recipeVersionRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    public Recipe forkRecipe(Integer originalRecipeId, User user, String changeDescription) {
        Recipe originalRecipe = recipeRepository.findById(originalRecipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));

        // Create a new recipe as a copy
        Recipe forkedRecipe = new Recipe();
        forkedRecipe.setTitle(originalRecipe.getTitle() + " (Forked)");
        forkedRecipe.setDescription(originalRecipe.getDescription());
        forkedRecipe.setIngredients(originalRecipe.getIngredients());
        forkedRecipe.setInstructions(originalRecipe.getInstructions());
        forkedRecipe.setUser(user);
        forkedRecipe.setImageURL(originalRecipe.getImageURL());

        // Save the forked recipe
        forkedRecipe = recipeRepository.save(forkedRecipe);

        // Create version record
        RecipeVersion version = new RecipeVersion();
        version.setOriginalRecipe(originalRecipe);
        version.setForkedRecipe(forkedRecipe);
        version.setUser(user);
        version.setForkedAt(LocalDateTime.now());
        version.setChangeDescription(changeDescription);
        version.setVersionNumber(generateVersionNumber(originalRecipe));

        recipeVersionRepository.save(version);

        return forkedRecipe;
    }

    public List<RecipeVersion> getForks(Integer recipeId) {
        return recipeVersionRepository.findByOriginalRecipe_RecipeID(recipeId);
    }

    public Recipe getOriginalRecipe(Integer recipeId) {
        List<RecipeVersion> versions = recipeVersionRepository.findByForkedRecipe_RecipeID(recipeId);
        if (!versions.isEmpty()) {
            return versions.get(0).getOriginalRecipe();
        }
        return null;
    }

    private String generateVersionNumber(Recipe originalRecipe) {
        long forkCount = recipeVersionRepository.findByOriginalRecipe_RecipeID(originalRecipe.getRecipeID()).size();
        return "v1." + (forkCount + 1);
    }
}