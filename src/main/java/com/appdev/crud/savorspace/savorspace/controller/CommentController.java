package com.appdev.crud.savorspace.savorspace.controller;

import com.appdev.crud.savorspace.savorspace.entity.CommentEntity;
import com.appdev.crud.savorspace.savorspace.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/comments")
@CrossOrigin(origins = {"http://localhost:5174","http://localhost:5173"})
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public List<CommentEntity> getAllComments() {
        return commentService.getAllComments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentEntity> getCommentById(@PathVariable Long id) {
        Optional<CommentEntity> comment = commentService.getCommentById(id);
        return comment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @PostMapping("/create")
    public ResponseEntity<CommentEntity> createComment(@RequestBody CommentEntity comment) {
        try {
            CommentEntity savedComment = commentService.createComment(comment);
            return ResponseEntity.ok(savedComment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/print")
    public String print() {
        return "Hello World!";
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentEntity> updateComment(@PathVariable Long id, @RequestBody CommentEntity comment) {
        CommentEntity updatedComment = commentService.updateComment(id, comment);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}

