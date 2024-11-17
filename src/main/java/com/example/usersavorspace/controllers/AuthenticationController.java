package com.example.usersavorspace.controllers;

import com.example.usersavorspace.dtos.LoginResponse;
import com.example.usersavorspace.dtos.LoginUserDto;
import com.example.usersavorspace.dtos.PasswordResetRequest;
import com.example.usersavorspace.entities.Contact;
import com.example.usersavorspace.entities.User;
import com.example.usersavorspace.services.AuthenticationService;
import com.example.usersavorspace.services.EmailService;
import com.example.usersavorspace.services.JwtService;
import com.example.usersavorspace.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/auth")
@RestController
@CrossOrigin
public class AuthenticationController {

    @Autowired
    private EmailService emailService;

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    private final UserService userService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, UserService userService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userService = userService;
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        try {
            Optional<User> userOptional = userService.findByEmail(request.getEmail());

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "User not found"));
            }

            User user = userOptional.get();

            // Encrypt the new password using BCryptPasswordEncoder
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(request.getNewPassword());

            // Update the user's password
            user.setPassword(encodedPassword);
            userService.save(user);

            return ResponseEntity.ok()
                    .body(Map.of("message", "Password reset successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error resetting password"));
        }
    }

    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyToken() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if(authentication != null && authentication.isAuthenticated()) {
                return ResponseEntity.ok(Map.of("valid", true));
            }
            return ResponseEntity.ok(Map.of("valid", false));
        }catch (Exception e) {
            return ResponseEntity.ok(Map.of("valid", false));
        }
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
                .setExpiresIn(jwtService.getExpirationTime())
                .setUserId(authenticatedUser.getId());

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

    @PostMapping("/create-admin")
    public ResponseEntity<User> createAdmin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("fullName") String fullName,
            @RequestParam("profilePic") MultipartFile profilePic) {
        User adminUser = authenticationService.signup(email, password, fullName, profilePic, "ADMIN");
        return ResponseEntity.ok(adminUser);
    }

    @PostMapping("/login-admin")
    public ResponseEntity<LoginResponse> authenticateAdmin(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        if(!"ADMIN".equals(authenticatedUser.getRole())) {
            return ResponseEntity.status(403).body(null);
        }

        String jwtToken = jwtService.generateToken(authenticatedUser);
        String refreshToken = jwtService.generateRefreshToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse()
                .setToken(jwtToken)
                .setRefreshToken(refreshToken)
                .setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/logout/github")
    public ResponseEntity<Void> logoutGithub(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate();
        }

        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("https://github.com/logout")).build();
    }

    @PostMapping("/reactivate")
    public ResponseEntity<LoginResponse> reactivateAccount(@RequestBody LoginUserDto loginUserDto) {
        try {
            User reactivatedUser = authenticationService.reactivateAccount(
                    loginUserDto.getEmail(),
                    loginUserDto.getPassword()
            );

            // Generate new tokens
            String jwtToken = jwtService.generateToken(reactivatedUser);
            String refreshToken = jwtService.generateRefreshToken(reactivatedUser);

            LoginResponse loginResponse = new LoginResponse()
                    .setToken(jwtToken)
                    .setRefreshToken(refreshToken)
                    .setExpiresIn(jwtService.getExpirationTime())
                    .setUserId(reactivatedUser.getId())
                    .setUser(reactivatedUser);
            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException e) {
            LoginResponse errorResponse = new LoginResponse()
                    .setError(e.getMessage())
                    .setStatus("error");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateAccount(@RequestBody LoginUserDto loginUserDto) {
        try {
            User deactivatedUser = authenticationService.deactivateAccount(
                    loginUserDto.getEmail(),
                    loginUserDto.getPassword()
            );

            return ResponseEntity.ok()
                    .body(Map.of(
                            "message", "Account deactivated successfully.",
                            "email", deactivatedUser.getEmail()
                    ));
        }catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}