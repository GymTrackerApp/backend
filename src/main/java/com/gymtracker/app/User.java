package com.gymtracker.app;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    private UUID userId;
    private String username;
    private String email;
    private String passwordHash;
    private Instant createdAt;
}
