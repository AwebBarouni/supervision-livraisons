package com.supervision.livraisons.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.supervision.livraisons.dto.UserRequest;
import com.supervision.livraisons.model.User;
import com.supervision.livraisons.repository.UserRepository;

@Service
public class UserService {

    private static final String ROLE_LIVREUR = "LIVREUR";
    private static final String ROLE_CONTROLEUR = "CONTROLEUR";
    private static final Set<String> ALLOWED_ROLES = Set.of(ROLE_LIVREUR, ROLE_CONTROLEUR);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getCurrentUser(String userId) {
        return findById(userId);
    }

    public User getUserById(String userId) {
        return findById(userId);
    }

    public List<User> getLivreurs() {
        return userRepository.findByRoleOrderByNameAsc(ROLE_LIVREUR);
    }

    public User createUser(String requesterRole, UserRequest request) {
        if (!ROLE_CONTROLEUR.equals(requesterRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acces reserve au controleur");
        }
        if (request == null
                || !StringUtils.hasText(request.getName())
                || !StringUtils.hasText(request.getEmail())
                || !StringUtils.hasText(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Champs obligatoires manquants");
        }

        String role = StringUtils.hasText(request.getRole())
                ? request.getRole().trim().toUpperCase()
                : ROLE_LIVREUR;
        if (!ALLOWED_ROLES.contains(role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role invalide");
        }

        if (userRepository.findByEmail(request.getEmail().trim().toLowerCase()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est deja utilise");
        }

        String initials = request.getName().trim().replace(" ", "+");
        String avatarUrl = "https://ui-avatars.com/api/?name=" + initials + "&background=1A73E8&color=fff";

        User user = new User(null,
                request.getName().trim(),
                request.getEmail().trim().toLowerCase(),
                passwordEncoder.encode(request.getPassword()),
                role,
                avatarUrl,
                new Date());
        return userRepository.save(user);
    }

    public User updateUser(String requesterId, String requesterRole, String targetUserId, UserRequest request) {
        boolean isSelf = requesterId.equals(targetUserId);
        boolean isController = ROLE_CONTROLEUR.equals(requesterRole);

        if (!isSelf && !isController) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acces refuse");
        }

        User user = findById(targetUserId);

        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Corps de requete invalide");
        }

        if (StringUtils.hasText(request.getName())) {
            user.setName(request.getName().trim());
        }
        if (StringUtils.hasText(request.getEmail())) {
            String newEmail = request.getEmail().trim().toLowerCase();
            if (!newEmail.equals(user.getEmail())) {
                if (userRepository.findByEmail(newEmail).isPresent()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est deja utilise");
                }
                user.setEmail(newEmail);
            }
        }
        if (StringUtils.hasText(request.getPassword())) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        if (isController && StringUtils.hasText(request.getRole())) {
            String role = request.getRole().trim().toUpperCase();
            if (!ALLOWED_ROLES.contains(role)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role invalide");
            }
            user.setRole(role);
        }

        // Regenerate avatar URL when name changes
        if (StringUtils.hasText(request.getName())) {
            String initials = user.getName().replace(" ", "+");
            user.setAvatarUrl("https://ui-avatars.com/api/?name=" + initials + "&background=1A73E8&color=fff");
        }

        return userRepository.save(user);
    }

    public void deleteUser(String requesterRole, String targetUserId) {
        if (!ROLE_CONTROLEUR.equals(requesterRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acces reserve au controleur");
        }
        User user = findById(targetUserId);
        userRepository.delete(user);
    }

    private User findById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
    }
}
