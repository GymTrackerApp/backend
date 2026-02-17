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
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    @Override
    public Exercise createCustomExercise(Exercise exercise, UUID ownerId) {
        if (exerciseRepository.existsByNameAndOwnerIsNull(exercise.getName()))
            throw new ExerciseAlreadyExistsException(
                    messageSource.getMessage(
                            "exercise.exists.predefined",
                            new Object[]{exercise.getName()},
                            LocaleContextHolder.getLocale()
                    )
            );

        User owner = userRepository.findById(ownerId)
                .orElseThrow(
                        () -> new UserDoesNotExistException(
                                messageSource.getMessage("user-does-not-exist-exception.owner-not-found", null, LocaleContextHolder.getLocale())
                        )
                );

        Exercise customExercise = owner.createCustomExercise(exercise.getName(), exercise.getCategory());

        return exerciseRepository.save(customExercise);
    }

    @Override
    public Set<Exercise> getUserExercises(UUID ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(
                        () -> new UserDoesNotExistException(
                                messageSource.getMessage("user-does-not-exist-exception.owner-not-found", null, LocaleContextHolder.getLocale())
                        )
                );

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
                .orElseThrow(
                        () -> new UserDoesNotExistException(
                                messageSource.getMessage(
                                        "user-does-not-exist-exception.deleting-exercise-for-non-existing-user",
                                        null,
                                        LocaleContextHolder.getLocale()
                                )
                        )
                );

        user.removeCustomExercise(exerciseId);

        exerciseRepository.deleteById(exerciseId);
    }

    @Override
    @Transactional
    public Exercise updateCustomExercise(long exerciseId, ExerciseCreationRequest exerciseCreationRequest, UUID userId) {
        if (exerciseRepository.existsByNameAndOwnerIsNull(exerciseCreationRequest.name()))
            throw new ExerciseAlreadyExistsException(messageSource.getMessage(
                    "exercise-already-exists-exception.predefined-exercise",
                    null,
                    LocaleContextHolder.getLocale()
            ));

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new UserDoesNotExistException(
                                messageSource.getMessage(
                                        "user-does-not-exist-exception.updating-exercise-for-non-existing-user",
                                        null,
                                        LocaleContextHolder.getLocale()
                                )
                        )
                );

        Exercise updatedExercise = user.updateCustomExercise(exerciseId, exerciseCreationRequest.name(), exerciseCreationRequest.category());

        return exerciseRepository.save(updatedExercise);
    }
}
