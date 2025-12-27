package com.gymtracker.app.repository;

import com.gymtracker.app.domain.workout.Workout;

import java.util.List;
import java.util.UUID;

public interface WorkoutRepository {
    Workout save(Workout workout);
    List<Workout> findLastWorkoutsContainingExercise(long exerciseId, int previousWorkouts, UUID userId);
}
