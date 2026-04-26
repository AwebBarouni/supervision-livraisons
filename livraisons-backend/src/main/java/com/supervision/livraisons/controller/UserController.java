package com.supervision.livraisons.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supervision.livraisons.dto.UserRequest;
import com.supervision.livraisons.model.User;
import com.supervision.livraisons.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public User getMe(Authentication authentication) {
        return userService.getCurrentUser(currentUserId(authentication));
    }

    @GetMapping("/livreurs")
    @PreAuthorize("hasRole('CONTROLEUR')")
    public List<User> getLivreurs() {
        return userService.getLivreurs();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@RequestBody UserRequest request, Authentication authentication) {
        return userService.createUser(currentRole(authentication), request);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable String id,
                           @RequestBody UserRequest request,
                           Authentication authentication) {
        return userService.updateUser(currentUserId(authentication), currentRole(authentication), id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CONTROLEUR')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id, Authentication authentication) {
        userService.deleteUser(currentRole(authentication), id);
        return ResponseEntity.noContent().build();
    }

    private String currentUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return authentication.getName();
    }

    private String currentRole(Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String value = authority.getAuthority();
            if (value.startsWith("ROLE_")) {
                return value.substring(5);
            }
        }
        return "";
    }
}
