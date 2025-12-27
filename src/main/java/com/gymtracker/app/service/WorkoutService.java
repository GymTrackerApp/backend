package com.gymtracker.app.service;

import com.gymtracker.app.domain.workout.Workout;
import com.gymtracker.app.dto.request.WorkoutCreationRequest;

import java.util.List;
import java.util.UUID;

public interface WorkoutService {
    void createWorkout(WorkoutCreationRequest workoutCreationRequest, UUID userId);
    List<Workout> getWorkoutExerciseHistory(long exerciseId, int previousWorkouts, UUID userId);
}
