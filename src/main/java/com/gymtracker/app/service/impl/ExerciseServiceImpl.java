package com.gymtracker.app.service.impl;

import com.gymtracker.app.domain.Exercise;
import com.gymtracker.app.domain.User;
import com.gymtracker.app.dto.request.ExerciseCreationRequest;
import com.gymtracker.app.exception.ExerciseAlreadyExistsException;
import com.gymtracker.app.exception.UserDoesNotExistException;
import com.gymtracker.app.repository.ExerciseRepository;
import com.gymtracker.app.repository.UserRepository;
import com.gymtracker.app.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    @Override
    public Exercise createCustomExercise(Exercise exercise, UUID ownerId) {
        if (exerciseRepository.existsByNameAndOwnerUserId(exercise.getName(), ownerId))
            throw new ExerciseAlreadyExistsException(String.format("Exercise with name '%s' already exists in your exercises",  exercise.getName()));
        else if (exerciseRepository.existsByNameAndOwnerIsNull(exercise.getName()))
            throw new ExerciseAlreadyExistsException(String.format("Exercise with name '%s' already exists in predefined exercises",  exercise.getName()));

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserDoesNotExistException("Owner not found"));

        Exercise customExercise = owner.createCustomExercise(exercise.getName(), exercise.getCategory());

        return exerciseRepository.save(customExercise);
    }

    @Override
    public Set<Exercise> getUserExercises(UUID ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserDoesNotExistException("Owner not found"));

        return owner.getExercises();
    }

    @Override
    public Set<Exercise> getPredefinedExercises() {
        return exerciseRepository.findAllPredefinedExercises();
    }

    @Override
    @Transactional
    public void deleteCustomExercise(long exerciseId, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("Cannot delete exercise for non-existing user"));

        user.removeCustomExercise(exerciseId);

        exerciseRepository.deleteById(exerciseId);
    }

    @Override
    @Transactional
    public Exercise updateCustomExercise(long exerciseId, ExerciseCreationRequest exerciseCreationRequest, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("Cannot update exercise for non-existing user"));

        Exercise updatedExercise = user.updateCustomExercise(exerciseId, exerciseCreationRequest.name(), exerciseCreationRequest.category());

        return exerciseRepository.save(updatedExercise);
    }
}
