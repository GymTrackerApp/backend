package com.gymtracker.app.repository;

import com.gymtracker.app.domain.Exercise;

import java.util.UUID;

public interface ExerciseRepository {
    boolean existsByNameAndOwnerUserId(String name, UUID ownerId);
    boolean existsByNameAndOwnerIsNull(String name);
    Exercise save(Exercise exercise);
}
