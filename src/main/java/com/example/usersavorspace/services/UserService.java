package com.example.usersavorspace.services;


import com.example.usersavorspace.entities.User;
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


    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        }catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
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
        return userRepository.findByEmail(email);
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
        userRepository.deleteById(id);
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
}