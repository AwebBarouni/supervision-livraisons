package com.supervision.livraisons.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.supervision.livraisons.model.User;
import com.supervision.livraisons.repository.UserRepository;

@Service
public class UserService {

    private static final String ROLE_LIVREUR = "LIVREUR";

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
    }

    public List<User> getLivreurs() {
        return userRepository.findByRoleOrderByNameAsc(ROLE_LIVREUR);
    }
}
