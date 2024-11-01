package com.example.usersavorspace.controllers;

import com.example.usersavorspace.dtos.LoginResponse;
import com.example.usersavorspace.dtos.LoginUserDto;
import com.example.usersavorspace.entities.Contact;
import com.example.usersavorspace.entities.User;
import com.example.usersavorspace.services.AuthenticationService;
import com.example.usersavorspace.services.EmailService;
import com.example.usersavorspace.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RequestMapping("/auth")
@RestController
@CrossOrigin
public class AuthenticationController {

    @Autowired
    private EmailService emailService;

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("fullName") String fullName,
            @RequestParam("profilePic") MultipartFile profilePic) {
        User registeredUser = authenticationService.signup(email, password, fullName, profilePic);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);
        String refreshToken = jwtService.generateRefreshToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse()
                .setToken(jwtToken)
                .setRefreshToken(refreshToken)
                .setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (jwtService.isRefreshTokenValid(refreshToken,
                authenticationService.loadUserByUsername(jwtService.extractUsername(refreshToken)))) {
            String newToken = jwtService.generateToken(
                    authenticationService.loadUserByUsername(jwtService.extractUsername(refreshToken)));
            return ResponseEntity.ok(Map.of("token", newToken));
        } else {
            return ResponseEntity.status(403).body("Invalid refresh token");
        }
    }

    @PostMapping("/email")
    public void saveContact(@RequestBody Contact contact) {
        //sends the email
        String subject = "New Contact Message";
        String text = "You have a new message from: " + contact.getFirstName() + " " + contact.getLastName()
                + "\nEmail: " + contact.getEmail()
                + "\nConcern: " + contact.getConcern();
        emailService.sendSimpleMessage("savorspaceproject@gmail.com", subject, text);
    }
}