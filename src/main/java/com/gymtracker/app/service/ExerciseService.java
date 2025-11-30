package com.gymtracker.app.service;

import com.gymtracker.app.dto.request.ExerciseCreationRequest;
import com.gymtracker.app.dto.response.UserExerciseDTO;
import com.gymtracker.app.entity.User;

import java.util.UUID;

public interface ExerciseService {
    UserExerciseDTO createExercise(ExerciseCreationRequest exerciseCreationRequest, User owner);
}
