package com.appdev.crud.savorspace.savorspace.service;

import com.appdev.crud.savorspace.savorspace.entity.CommentEntity;
import com.appdev.crud.savorspace.savorspace.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public List<CommentEntity> getAllComments() {
        return commentRepository.findAll();
    }

    public Optional<CommentEntity> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    public CommentEntity createComment(CommentEntity comment) {
        if (comment.getCreatedAt() == null) {
            comment.setCreatedAt(LocalDateTime.now());
        }
        return commentRepository.save(comment);
    }

    public CommentEntity updateComment(Long id, CommentEntity updatedComment) {
        return commentRepository.findById(id)
                .map(comment -> {
                    comment.setRecipeID(updatedComment.getRecipeID());
                    comment.setUserID(updatedComment.getUserID());
                    comment.setContent(updatedComment.getContent());
                    comment.setIsFlagged(updatedComment.getIsFlagged());
                    if (updatedComment.getCreatedAt() != null) {
                        comment.setCreatedAt(updatedComment.getCreatedAt());
                    }
                    return commentRepository.save(comment);
                })
                .orElseGet(() -> {
                    updatedComment.setCommentID(id);
                    return commentRepository.save(updatedComment);
                });
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}