package com.example.usersavorspace.services;


import com.example.usersavorspace.entities.User;
import com.example.usersavorspace.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    public User updateUser(Integer id, User user) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User updatedUser = existingUser.get();

            // Only update fields that are provided
            if (user.getFullName() != null) {
                updatedUser.setFullName(user.getFullName());
            }
            if (user.getEmail() != null) {
                updatedUser.setEmail(user.getEmail());
            }
            if (user.getImageURL() != null) {
                updatedUser.setImageURL(user.getImageURL());
            }
            if (user.getRole() != null) {
                updatedUser.setRole(user.getRole());
            }

            return userRepository.save(updatedUser);
        } else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }
}