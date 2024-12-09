package com.example.usersavorspace.controllers;

import com.example.usersavorspace.dtos.RecipeVersionDTO;
import com.example.usersavorspace.entities.Recipe;
import com.example.usersavorspace.entities.RecipeVersion;
import com.example.usersavorspace.entities.User;
import com.example.usersavorspace.services.RecipeVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipes")
public class RecipeVersionController {
    @Autowired
    private RecipeVersionService recipeVersionService;

    @PostMapping("/{recipeId}/fork")
    public ResponseEntity<?> forkRecipe(
            @PathVariable Integer recipeId,
            @RequestBody String changeDescription,
            @AuthenticationPrincipal User user) {

        Recipe forkedRecipe = recipeVersionService.forkRecipe(recipeId, user, changeDescription);
        return ResponseEntity.ok(forkedRecipe);
    }

    @GetMapping("/{recipeId}/forks")
    public ResponseEntity<List<RecipeVersionDTO>> getRecipeForks(@PathVariable Integer recipeId) {
        List<RecipeVersion> forks = recipeVersionService.getForks(recipeId);
        List<RecipeVersionDTO> dtos = forks.stream()
                .map(RecipeVersionDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{recipeId}/original")
    public ResponseEntity<?> getOriginalRecipe(@PathVariable Integer recipeId) {
        Recipe originalRecipe = recipeVersionService.getOriginalRecipe(recipeId);
        if (originalRecipe != null) {
            return ResponseEntity.ok(originalRecipe);
        }
        return ResponseEntity.notFound().build();
    }
}