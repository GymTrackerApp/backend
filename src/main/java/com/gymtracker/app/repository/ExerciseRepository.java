package com.gymtracker.app.repository;

import com.gymtracker.app.domain.Exercise;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ExerciseRepository {
    boolean existsByNameAndOwnerUserId(String name, UUID ownerId);
    boolean existsByNameAndOwnerIsNull(String name);
    Exercise save(Exercise exercise);
    Set<Exercise> findAllPredefinedExercises();
    Optional<Exercise> findExerciseAccessibleByUser(Long exerciseId, UUID userId);
}
