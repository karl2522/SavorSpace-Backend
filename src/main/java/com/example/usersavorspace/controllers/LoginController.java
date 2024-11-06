package com.example.usersavorspace.controllers;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/")
    public String index() {
        return "index";  // Login page or landing page
    }

    @GetMapping("/home")
    public String home(OAuth2AuthenticationToken authentication) {
        return "home";  // Thymeleaf view for home
    }
}
