package com.supervision.livraisons.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.supervision.livraisons.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        com.supervision.livraisons.model.User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

        List<String> authorities = new ArrayList<>();
        if ("LIVREUR".equalsIgnoreCase(user.getRole())) {
            authorities.add("ROLE_LIVREUR");
            authorities.add("ROLE_DRIVER");
        } else {
            authorities.add("ROLE_" + user.getRole());
        }

        return User.withUsername(user.getId())
                .password(user.getPasswordHash())
                .authorities(authorities.toArray(new String[0]))
                .build();
    }
}
