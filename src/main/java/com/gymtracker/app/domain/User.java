package com.gymtracker.app.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class User implements UserDetails {
    private final UUID userId;
    private String username;
    private String email;

    @Setter(AccessLevel.NONE)
    private Password password;
    private Set<Exercise> exercises;

    public void updatePassword(String passwordHash) {
        this.password = new Password(passwordHash);
    }

    public Exercise createCustomExercise(String name, ExerciseCategory category) {
        return Exercise.builder()
                .name(name)
                .isCustom(true)
                .ownerId(this.userId)
                .category(category)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password.hashedPassword();
    }

    @Override
    public String getUsername() {
        return userId.toString();
    }

    public String getDisplayUsername() {
        return username;
    }
}
