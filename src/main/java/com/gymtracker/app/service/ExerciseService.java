package com.gymtracker.app.service;

import com.gymtracker.app.domain.Exercise;

import java.util.UUID;

public interface ExerciseService {
    Exercise createCustomExercise(Exercise exercise, UUID ownerId);
}
