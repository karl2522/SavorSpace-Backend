package com.example.usersavorspace.services;


import com.example.usersavorspace.entities.Comment;
import com.example.usersavorspace.entities.User;
import com.example.usersavorspace.repositories.CommentRepository;
import com.example.usersavorspace.repositories.RatingRepository;
import com.example.usersavorspace.repositories.RecipeRepository;
import com.example.usersavorspace.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final Path fileStorageLocation;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CommentRepository commentRepository;
    private final RecipeRepository recipeRepository;
    private final RatingRepository ratingRepository;


    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, CommentRepository commentRepository, RecipeRepository recipeRepository, RatingRepository ratingRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        }catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
        this.commentRepository = commentRepository;
        this.recipeRepository = recipeRepository;
        this.ratingRepository = ratingRepository;
    }

    public User deactivateAccount(Integer userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }

        user.setActive(false);
        return userRepository.save(user);
    }

    public User reactivateAccount(Integer userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }

        user.setActive(true);
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailAndDeletedFalse(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found id: " + id));


        List<Comment> userComments = commentRepository.findByUserIdAndDeletedFalse(id);
        if(userComments != null && !userComments.isEmpty()) {
            userComments.forEach(comment -> {
                comment.setDeleted(true);
                commentRepository.save(comment);
            });
        }


        user.setActive(false);
        user.setDeleted(true);
        userRepository.save(user);
    }

    public User adminUpdateUser(Integer id, User user) {
        Optional<User> existingUser = userRepository.findById(id);
        if(existingUser.isPresent()) {

            User updatedUser = existingUser.get();

            if(user.getFullName() != null) {
                updatedUser.setFullName(user.getFullName());
            }
            if(user.getEmail() != null) {
                updatedUser.setEmail(user.getEmail());
            }

            if(user.getRole() != null) {
                updatedUser.setRole(user.getRole());
            }

            return userRepository.save(updatedUser);
        }else {
            throw new RuntimeException("User not found id: " + id);
        }
    }

    public User updateUser(Integer id, User userDetails, MultipartFile newProfilePic) {
        Optional<User> existingUser = userRepository.findById(id);
        if(existingUser.isPresent()) {

            User updatedUser = existingUser.get();

            if(userDetails.getFullName() != null) {
                updatedUser.setFullName(userDetails.getFullName());
            }
            if(userDetails.getEmail() != null) {
                updatedUser.setEmail(userDetails.getEmail());
            }
            if(userDetails.getRole() != null) {
                updatedUser.setRole(userDetails.getRole());
            }

            if(newProfilePic != null && !newProfilePic.isEmpty()) {
                try {

                    if(updatedUser.getImageURL() != null) {
                        Path oldFile = Paths.get("." + updatedUser.getImageURL());
                        Files.deleteIfExists(oldFile);
                    }

                    String fileName = System.currentTimeMillis() + "_" + newProfilePic.getOriginalFilename();
                    Path targetLocation = this.fileStorageLocation.resolve(fileName);
                    Files.copy(newProfilePic.getInputStream(), targetLocation);

                    updatedUser.setImageURL("/uploads/" + fileName);
                }catch (IOException ex) {
                    throw new RuntimeException("Could not store file " + newProfilePic.getOriginalFilename() + ". Please try again!", ex);
                }
            }

            return userRepository.save(updatedUser);
        }else {
            throw new RuntimeException("User not found id: " + id);
        }
    }


    public List<User> getAllDeactivatedAccounts() {
        return userRepository.findByActiveFalse();
    }

    public List<User> findAllActiveUsers() {
        return userRepository.findByActiveIsTrueAndRoleAndDeletedFalse("USER");
    }

    public List<User> findAllDeletedUsers() {
        return userRepository.findByDeletedTrueAndRole("USER");
    }

    public User restoreUser(Integer userId) {
        User user = userRepository.findByIdAndDeletedTrue(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        List<Comment> userComments = commentRepository.findByUserIdAndDeletedFalse(userId);
        if(userComments != null && !userComments.isEmpty()) {
            userComments.forEach(comment -> {
                comment.setDeleted(false);
                commentRepository.save(comment);
            });
        }

        user.setActive(true);
        user.setDeleted(false);
        return userRepository.save(user);
    }

    public long getUserRecipeCount(Integer userId) {
        return recipeRepository.countByUserId(userId);
    }

    public long getUserCommentCount(Integer userId) {
        return commentRepository.countByUserId(userId);
    }

    public long getUserRatingCount(Integer userId) {
        return ratingRepository.countByUserId(userId);
    }
}