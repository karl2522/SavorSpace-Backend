package com.example.usersavorspace.services;

import com.example.usersavorspace.dtos.LoginUserDto;
import com.example.usersavorspace.entities.User;
import com.example.usersavorspace.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final Path fileStorageLocation;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public User signup(String email, String password, String fullName, MultipartFile profilePic) {
        return signup(email, password, fullName, profilePic, "USER");
    }

    public User signup(String email, String password, String fullName, MultipartFile profilePic, String role) {
        String fileName = profilePic.getOriginalFilename();
        Path targetLocation = this.fileStorageLocation.resolve(fileName);

        try {
            Files.copy(profilePic.getInputStream(), targetLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }

        User user = new User()
                .setFullName(fullName)
                .setEmail(email)
                .setPassword(passwordEncoder.encode(password))
                .setImageURL("/uploads/" + fileName)
                .setRole(role);

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }

    public User loadUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    public User createAdmin(String email, String password, String fullName, MultipartFile profilePic) {
        return signup(email, password, fullName, profilePic, "ADMIN");
    }
}