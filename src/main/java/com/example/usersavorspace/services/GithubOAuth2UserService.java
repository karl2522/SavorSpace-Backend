package com.example.usersavorspace.services;

import com.example.usersavorspace.entities.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
public class GithubOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    public GithubOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");
        System.out.println("OAuth2 Attributes: " + oAuth2User.getAttributes());

        // If email is null, try to fetch from GitHub API
        if (email == null) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                String accessToken = userRequest.getAccessToken().getTokenValue();
                String emailEndpoint = "https://api.github.com/user/emails";

                List<Map<String, Object>> emails = restTemplate.exchange(
                        emailEndpoint,
                        HttpMethod.GET,
                        new HttpEntity<>(createHeaders(accessToken)),
                        List.class
                ).getBody();

                if (emails != null && !emails.isEmpty()) {
                    for (Map<String, Object> emailEntry : emails) {
                        if (Boolean.TRUE.equals(emailEntry.get("primary")) &&
                                Boolean.TRUE.equals(emailEntry.get("verified"))) {
                            email = (String) emailEntry.get("email");
                            break;
                        }
                    }

                    if (email == null && !emails.isEmpty()) {
                        email = (String) emails.get(0).get("email");
                    }
                }
            } catch (Exception e) {
                System.err.println("Error fetching GitHub emails: " + e.getMessage());
            }
        }

        // If still no email, generate a placeholder email using the GitHub username
        String username = oAuth2User.getAttribute("login");
        if (email == null && username != null) {
            email = username + "@github.user";
        }

        String avatarUrl = oAuth2User.getAttribute("avatar_url");
        String name = oAuth2User.getAttribute("name");

        String finalEmail = email;
        User user = userService.findByEmail(finalEmail)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(finalEmail);
                    newUser.setFullName(name != null ? name : username);
                    newUser.setImageURL(avatarUrl);
                    newUser.setRole("USER");
                    newUser.setPassword(UUID.randomUUID().toString());
                    return userService.save(newUser);
                });

        return new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole())),
                oAuth2User.getAttributes(),
                "login"
        );
    }

    private HttpHeaders createHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.add("Accept", "application/vnd.github.v3+json");
        return headers;
    }
}
