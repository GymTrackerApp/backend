package com.gymtracker.app.unit;

import com.gymtracker.app.domain.Exercise;
import com.gymtracker.app.domain.User;
import com.gymtracker.app.entity.UserEntity;
import com.gymtracker.app.exception.ExerciseAlreadyExistsException;
import com.gymtracker.app.repository.ExerciseRepository;
import com.gymtracker.app.repository.UserRepository;
import com.gymtracker.app.service.impl.ExerciseServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {
    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExerciseServiceImpl exerciseService;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(exerciseService);
    }

    @Test
    void givenNewExercise_whenCreateExerciseCalled_shouldSaveCustomExercise() {
        Exercise exercise = Exercise.builder()
                .name("My new exercise")
                .build();

        Mockito.when(userRepository.findById(any()))
                .thenReturn(
                        Optional.of(User.builder().build())
                );

        exerciseService.createCustomExercise(exercise, UUID.randomUUID());

        verify(exerciseRepository).save(any());
    }

    @Test
    void givenExerciseAlreadyCreatedByUser_whenCreateCustomExerciseCalled_shouldThrowException() {
        Exercise exercise = Exercise.builder()
                .name("My new exercise")
                .build();
        UserEntity owner = new UserEntity();
        owner.setUserId(UUID.randomUUID());

        when(exerciseRepository.existsByNameAndOwnerUserId(exercise.getName(), owner.getUserId()))
                .thenReturn(true);

        Assertions.assertThrows(ExerciseAlreadyExistsException.class, () -> exerciseService.createCustomExercise(exercise, owner.getUserId()));
    }

    @Test
    void givenExerciseExistingInPredefinedExercises_whenCreateCustomExerciseCalled_shouldThrowException() {
        Exercise exercise = Exercise.builder()
                .name("My new exercise")
                .build();

        UserEntity owner = new UserEntity();
        owner.setUserId(UUID.randomUUID());

        when(exerciseRepository.existsByNameAndOwnerIsNull(exercise.getName()))
                .thenReturn(true);

        Assertions.assertThrows(ExerciseAlreadyExistsException.class, () -> exerciseService.createCustomExercise(exercise, owner.getUserId()));
    }
}
