package com.gymtracker.app.service.impl;

import com.gymtracker.app.dto.request.ExerciseCreationRequest;
import com.gymtracker.app.dto.response.UserExerciseDTO;
import com.gymtracker.app.entity.Exercise;
import com.gymtracker.app.entity.User;
import com.gymtracker.app.exception.ExerciseAlreadyExistsException;
import com.gymtracker.app.mapper.ExerciseMapper;
import com.gymtracker.app.repository.ExerciseRepository;
import com.gymtracker.app.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {
    private final ExerciseMapper exerciseMapper;
    private final ExerciseRepository exerciseRepository;

    @Override
    public UserExerciseDTO createExercise(ExerciseCreationRequest exerciseCreationRequest, User owner) {
        if (exerciseRepository.existsByNameAndOwnerUserId(exerciseCreationRequest.name(), owner.getUserId()))
            throw new ExerciseAlreadyExistsException(String.format("Exercise with name %s already exists in your exercises",  exerciseCreationRequest.name()));
        else if (exerciseRepository.existsByNameAndOwnerIsNull(exerciseCreationRequest.name()))
            throw new ExerciseAlreadyExistsException(String.format("Exercise with name %s already exists in predefined exercises",  exerciseCreationRequest.name()));

        Exercise exercise = exerciseMapper.exerciseCreationRequestToExercise(exerciseCreationRequest);
        exercise.setCustom(true);
        exercise.setOwner(owner);

        Exercise savedExercise = exerciseRepository.save(exercise);

        return exerciseMapper.exerciseToUserExerciseDTO(savedExercise);
    }
}
