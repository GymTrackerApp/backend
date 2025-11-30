package com.gymtracker.app.unit;

import com.gymtracker.app.dto.request.ExerciseCreationRequest;
import com.gymtracker.app.entity.User;
import com.gymtracker.app.exception.ExerciseAlreadyExistsException;
import com.gymtracker.app.mapper.ExerciseMapper;
import com.gymtracker.app.repository.ExerciseRepository;
import com.gymtracker.app.service.impl.ExerciseServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {
    @Spy
    private ExerciseMapper exerciseMapper = Mappers.getMapper(ExerciseMapper.class);

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseServiceImpl exerciseService;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(exerciseService);
    }

    @Test
    void givenNewExercise_whenCreateExerciseCalled_shouldSaveExercise() {
        ExerciseCreationRequest exerciseCreationRequest = ExerciseCreationRequest.builder()
                .name("My new exercise")
                .build();

        exerciseService.createExercise(exerciseCreationRequest, new User());

        verify(exerciseRepository).save(any());
    }

    @Test
    void givenExerciseAlreadyCreatedByUser_whenCreateExerciseCalled_shouldThrowException() {
        ExerciseCreationRequest exerciseCreationRequest = ExerciseCreationRequest.builder()
                .name("My new exercise")
                .build();
        User owner = new User();

        when(exerciseRepository.existsByNameAndOwnerUserId(exerciseCreationRequest.name(), owner.getUserId()))
                .thenReturn(true);

        Assertions.assertThrows(ExerciseAlreadyExistsException.class, () -> exerciseService.createExercise(exerciseCreationRequest, owner));
    }

    @Test
    void givenExerciseExistingInPredefinedExercises_whenCreateExerciseCalled_shouldThrowException() {
        ExerciseCreationRequest exerciseCreationRequest = ExerciseCreationRequest.builder()
                .name("My new exercise")
                .build();

        User owner = new User();

        when(exerciseRepository.existsByNameAndOwnerIsNull(exerciseCreationRequest.name()))
                .thenReturn(true);

        Assertions.assertThrows(ExerciseAlreadyExistsException.class, () -> exerciseService.createExercise(exerciseCreationRequest, owner));
    }
}
