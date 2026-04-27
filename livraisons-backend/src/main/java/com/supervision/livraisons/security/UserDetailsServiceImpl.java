package com.supervision.livraisons.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

        List<GrantedAuthority> authorities = new ArrayList<>();
        if ("LIVREUR".equalsIgnoreCase(user.getRole())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_LIVREUR"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()));
        }

        return User.withUsername(user.getId())
                .password(user.getPasswordHash())
                .authorities(authorities.toArray(new GrantedAuthority[0]))
                .build();
    }
}