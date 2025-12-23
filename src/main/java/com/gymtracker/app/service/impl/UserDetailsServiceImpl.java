package com.gymtracker.app.service.impl;

import com.gymtracker.app.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UUID uuid;

        try {
            uuid = UUID.fromString(userId);
        } catch(IllegalArgumentException e) {
            throw new UsernameNotFoundException("Invalid user id format.");
        }

        return userRepository.findByIdWithoutCollections(uuid)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + uuid));
    }
}
