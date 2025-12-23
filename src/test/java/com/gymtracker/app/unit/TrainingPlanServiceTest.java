package com.gymtracker.app.unit;

import com.gymtracker.app.domain.Exercise;
import com.gymtracker.app.domain.User;
import com.gymtracker.app.dto.request.TrainingPlanCreationRequest;
import com.gymtracker.app.exception.ExerciseDoesNotExistException;
import com.gymtracker.app.exception.UserDoesNotExistException;
import com.gymtracker.app.repository.ExerciseRepository;
import com.gymtracker.app.repository.TrainingPlanRepository;
import com.gymtracker.app.repository.UserRepository;
import com.gymtracker.app.service.impl.TrainingPlanServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TrainingPlanServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TrainingPlanRepository trainingPlanRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private TrainingPlanServiceImpl trainingPlanService;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(trainingPlanService);
    }

    @Test
    void givenExercisesNonAccessibleByTheUser_whenGenerateCustomTrainingPlanCalled_shouldThrowExerciseDoesNotExistException() {
        Mockito.when(userRepository.findById(any()))
                        .thenReturn(Optional.of(User.builder().build()));

        Mockito.when(exerciseRepository.findExerciseAccessibleByUser(any(), any()))
                .thenReturn(Optional.empty());

        TrainingPlanCreationRequest trainingPlanCreationRequest = TrainingPlanCreationRequest.builder()
                .planItems(List.of(TrainingPlanCreationRequest.PlanItem.builder().exerciseId(1L).build()))
                .build();

        Assertions.assertThrows(ExerciseDoesNotExistException.class, () -> {
            trainingPlanService.generateCustomTrainingPlan(trainingPlanCreationRequest, UUID.randomUUID());
        });
    }

    @Test
    void givenNonExistingUserId_whenGenerateCustomTrainingPlanCalled_shouldThrowUserDoesNotExistException() {
        UUID userId = UUID.randomUUID();

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        TrainingPlanCreationRequest trainingPlanCreationRequest = createEmptyTrainingPlanCreationRequest();

        Assertions.assertThrows(UserDoesNotExistException.class, () -> {
            trainingPlanService.generateCustomTrainingPlan(trainingPlanCreationRequest, userId);
        });
    }

    @Test
    void givenValidData_whenGenerateCustomTrainingPlanCalled_shouldReturnNonNullResponse() {
        Mockito.when(userRepository.findById(any()))
                .thenReturn(Optional.of(User.builder().plans(Collections.emptySet()).build()));

        Mockito.when(exerciseRepository.findExerciseAccessibleByUser(any(), any()))
                .thenReturn(Optional.of(Exercise.builder().build()));

        TrainingPlanCreationRequest trainingPlanCreationRequest = TrainingPlanCreationRequest.builder()
                .planItems(List.of(TrainingPlanCreationRequest.PlanItem.builder().exerciseId(1L).build()))
                .build();

        trainingPlanService.generateCustomTrainingPlan(trainingPlanCreationRequest, UUID.randomUUID());

        Mockito.verify(trainingPlanRepository).save(any());
    }

    private TrainingPlanCreationRequest createEmptyTrainingPlanCreationRequest() {
        return TrainingPlanCreationRequest.builder()
                .planItems(List.of())
                .build();
    }
}
