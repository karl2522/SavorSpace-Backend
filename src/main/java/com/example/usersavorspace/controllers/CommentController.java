package com.example.usersavorspace.controllers;

import com.example.usersavorspace.dtos.CommentDTO;
import com.example.usersavorspace.entities.Comment;
import com.example.usersavorspace.exceptions.ResourceNotFoundException;
import com.example.usersavorspace.exceptions.UnauthorizedException;
import com.example.usersavorspace.services.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:5173")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping(value = "/recipe/{recipeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCommentsByRecipe(@PathVariable Integer recipeId) {
        try {
            //System.out.println("Fetching comments for recipe ID: " + recipeId); // Debug log
            List<CommentDTO> comments = commentService.getCommentsByRecipe(recipeId);
            //System.out.println("Found comments: " + comments.size()); // Debug log
            //System.out.println("Comments content: " + comments); // Debug log
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(comments);
        } catch (Exception e) {
            System.err.println("Error in getCommentsByRecipe: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ArrayList<>());
        }
    }

    @PostMapping(value = "/recipe/{recipeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentDTO> createComment(
            @PathVariable Integer recipeId,
            @RequestBody CommentDTO commentDTO,
            Authentication authentication) {
        try {
            CommentDTO savedComment = commentService.createComment(recipeId, commentDTO.getContent(), authentication);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(savedComment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(null);
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long commentId,
            Authentication authentication) {
        try {
            commentService.deleteComment(commentId, authentication);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("Comment deleted successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("Error deleting comment: " + e.getMessage()));
        }
    }

    @PutMapping("/{commentId}/flag")
    public ResponseEntity<?> flagComment(@PathVariable Long commentId, Authentication authentication) {
        try {
            Comment updatedComment = commentService.toggleCommentFlag(commentId, authentication);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(updatedComment);
        }catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}

class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}