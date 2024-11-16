package com.example.usersavorspace.controllers;

import com.example.usersavorspace.dtos.RecipeDTO;
import com.example.usersavorspace.dtos.UserDTO;
import com.example.usersavorspace.entities.Recipe;
import com.example.usersavorspace.services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recipes")
@CrossOrigin(origins = "http://localhost:5173")
public class RecipeController {

    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    public ResponseEntity<Recipe> addRecipe(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("ingredients") String ingredients,
            @RequestParam("instructions") String instructions,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestAttribute("userId") Integer userId) {

        try {
            Recipe recipe = new Recipe();
            recipe.setTitle(title);
            recipe.setDescription(description);
            recipe.setIngredients(ingredients);
            recipe.setInstructions(instructions);

            Recipe created = recipeService.addRecipe(recipe, userId, image);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        }catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error creating recipe",
                    e
            );
        }
    }

    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Recipe> recipes = recipeService.getAllRecipes(page, size);
        List<RecipeDTO> recipeDTOS = recipes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(recipeDTOS);
    }

    private RecipeDTO convertToDTO(Recipe recipe) {
        RecipeDTO dto = new RecipeDTO();

        String imageURL = recipe.getImageURL();
        if(imageURL != null && !imageURL.isEmpty()) {
            imageURL = imageURL.replaceAll("/uploads/+", "/uploads/");
            dto.setImageURL(imageURL);
        }

        dto.setRecipeID(recipe.getRecipeID());
        dto.setTitle(recipe.getTitle());
        dto.setDescription(recipe.getDescription());
        dto.setIngredients(recipe.getIngredients());
        dto.setInstructions(recipe.getInstructions());
        dto.setUpdatedAt(recipe.getUpdatedAt());

        if(recipe.getCreatedAt() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            dto.setCreatedAt(recipe.getCreatedAt().format(formatter));
        }

        if(recipe.getUser() != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(recipe.getUser().getId());
            userDTO.setUsername(recipe.getUser().getUsername());

            String userImageURL = recipe.getUser().getImageURL();
            if(userImageURL != null && !userImageURL.isEmpty()) {
                userImageURL = userImageURL.replaceAll("/uploads/+", "/uploads/");
                userDTO.setImageURL(userImageURL);  // Changed from setImageUrl to setImageURL
            } else {
                // Set a default profile picture URL
                userDTO.setImageURL("/src/images/defaultProfiles.png");
            }

            dto.setUser(userDTO);
        }

        return dto;
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable int recipeId) {
        return ResponseEntity.ok(recipeService.getRecipeById(recipeId));
    }

    @PutMapping("/{recipeId}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable int recipeId,
                                               @RequestBody Recipe recipe,
                                               @RequestAttribute("userId") Integer userId) {
        return ResponseEntity.ok(recipeService.updateRecipe(recipeId, recipe, userId));
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable int recipeId,
                                             @RequestAttribute("userId") Integer userId) {
        recipeService.deleteRecipe(recipeId, userId);
        return ResponseEntity.noContent().build();
    }

}
