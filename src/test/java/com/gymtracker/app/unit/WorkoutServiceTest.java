package com.gymtracker.app.unit;

import com.gymtracker.app.domain.Exercise;
import com.gymtracker.app.domain.workout.Workout;
import com.gymtracker.app.dto.request.WorkoutCreationRequest;
import com.gymtracker.app.dto.request.WorkoutItemDTO;
import com.gymtracker.app.dto.request.WorkoutRepetitionItemDTO;
import com.gymtracker.app.exception.DuplicatedExercisesException;
import com.gymtracker.app.exception.ExerciseDoesNotExistException;
import com.gymtracker.app.exception.TrainingDoesNotExistException;
import com.gymtracker.app.exception.UserDoesNotExistException;
import com.gymtracker.app.mapper.WorkoutItemMapper;
import com.gymtracker.app.mapper.WorkoutMapper;
import com.gymtracker.app.repository.ExerciseRepository;
import com.gymtracker.app.repository.TrainingPlanRepository;
import com.gymtracker.app.repository.UserRepository;
import com.gymtracker.app.repository.WorkoutRepository;
import com.gymtracker.app.service.impl.WorkoutServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {
    @InjectMocks
    private WorkoutServiceImpl workoutService;

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private TrainingPlanRepository trainingPlanRepository;

    @Spy
    private WorkoutMapper workoutMapper = Mappers.getMapper(WorkoutMapper.class);

    @Spy
    private WorkoutItemMapper workoutItemMapper = Mappers.getMapper(WorkoutItemMapper.class);

    private final ArgumentCaptor<Workout> argumentCaptor = ArgumentCaptor.forClass(Workout.class);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(workoutMapper, "workoutItemMapper", workoutItemMapper);
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(workoutService);
    }

    @Test
    void givenValidWorkoutCreationRequest_whenCreateWorkout_thenWorkoutIsCreated() {
        WorkoutCreationRequest workoutCreationRequest = generateSampleWorkoutCreationRequest();

        UUID userId = UUID.randomUUID();

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(exerciseRepository.findExerciseAccessibleByUser(workoutCreationRequest.workoutItems().getFirst().getExerciseId(), userId))
                .thenReturn(Optional.of(Exercise.builder().build()));

        Mockito.when(trainingPlanRepository.existsById(workoutCreationRequest.trainingId()))
                .thenReturn(true);

        workoutService.createWorkout(workoutCreationRequest, userId);

        Mockito.verify(workoutRepository).save(argumentCaptor.capture());

        Workout savedWorkout = argumentCaptor.getValue();
        Assertions.assertNotNull(savedWorkout);
        Assertions.assertEquals(userId, savedWorkout.getUserId());
    }

    @Test
    void givenNonExistingUserId_whenCreateWorkout_thenUserDoesNotExistExceptionIsThrown() {
        WorkoutCreationRequest workoutCreationRequest = generateSampleWorkoutCreationRequest();

        UUID userId = UUID.randomUUID();
        Mockito.when(userRepository.existsById(userId))
                .thenReturn(false);

        Assertions.assertThrows(UserDoesNotExistException.class, () -> {
            workoutService.createWorkout(workoutCreationRequest, userId);
        });
    }

    private WorkoutCreationRequest generateSampleWorkoutCreationRequest() {
        List<WorkoutRepetitionItemDTO.ExerciseSet> sets = List.of(
                WorkoutRepetitionItemDTO.ExerciseSet.builder()
                        .reps(10)
                        .weight(50.0)
                        .build(),
                WorkoutRepetitionItemDTO.ExerciseSet.builder()
                        .reps(8)
                        .weight(55.0)
                        .build()
        );



        List<WorkoutItemDTO> workoutItems = List.of(
                WorkoutRepetitionItemDTO.builder()
                        .exerciseId(1L)
                        .sets(sets)
                        .build()
        );

        return WorkoutCreationRequest.builder()
                .trainingId(1L)
                .workoutItems(workoutItems)
                .build();
    }

    @Test
    void givenNonExistingExerciseIdInWorkoutItem_whenCreateWorkout_thenExceptionIsThrown() {
        WorkoutCreationRequest workoutCreationRequest = generateSampleWorkoutCreationRequest();

        UUID userId = UUID.randomUUID();
        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(trainingPlanRepository.existsById(workoutCreationRequest.trainingId()))
                        .thenReturn(true);

        Mockito.when(exerciseRepository.findExerciseAccessibleByUser(workoutCreationRequest.workoutItems().getFirst().getExerciseId(), userId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ExerciseDoesNotExistException.class, () -> {
            workoutService.createWorkout(workoutCreationRequest, userId);
        });
    }

    @Test
    void givenNonExistingTrainingId_whenCreateWorkout_thenExceptionIsThrown() {
        WorkoutCreationRequest workoutCreationRequest = generateSampleWorkoutCreationRequest();

        UUID userId = UUID.randomUUID();
        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(trainingPlanRepository.existsById(workoutCreationRequest.trainingId()))
                .thenReturn(false);

        Assertions.assertThrows(
                TrainingDoesNotExistException.class,
                () -> workoutService.createWorkout(workoutCreationRequest, userId)
        );
    }

    @Test
    void givenDuplicatedExerciseIdsInWorkoutItems_whenCreateWorkout_thenExceptionIsThrown() {
        List<WorkoutItemDTO> workoutItems = List.of(
                WorkoutRepetitionItemDTO.builder()
                        .exerciseId(1L)
                        .sets(List.of())
                        .build(),
                WorkoutRepetitionItemDTO.builder()
                        .exerciseId(1L)
                        .sets(List.of())
                        .build()
        );

        WorkoutCreationRequest workoutCreationRequest = WorkoutCreationRequest.builder()
                .trainingId(1L)
                .workoutItems(workoutItems)
                .build();

        UUID userId = UUID.randomUUID();
        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(trainingPlanRepository.existsById(workoutCreationRequest.trainingId()))
                .thenReturn(true);

        Assertions.assertThrows(DuplicatedExercisesException.class, () -> {
            workoutService.createWorkout(workoutCreationRequest, userId);
        });
    }

    @Test
    void givenNonExistingUserId_whenGetWorkoutExerciseHistory_thenUserDoesNotExistExceptionIsThrown() {
        UUID userId = UUID.randomUUID();
        long exerciseId = 1L;
        int previousWorkouts = 5;

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(false);

        Assertions.assertThrows(UserDoesNotExistException.class, () -> {
            workoutService.getWorkoutExerciseHistory(exerciseId, previousWorkouts, userId);
        });
    }

    @Test
    void givenNonExistingExerciseId_whenGetWorkoutExerciseHistory_thenExerciseDoesNotExistExceptionIsThrown() {
        UUID userId = UUID.randomUUID();
        long exerciseId = 1L;
        int previousWorkouts = 5;

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(exerciseRepository.existsInExercisesAccessibleByUser(exerciseId, userId))
                .thenReturn(false);

        Assertions.assertThrows(ExerciseDoesNotExistException.class, () -> {
            workoutService.getWorkoutExerciseHistory(exerciseId, previousWorkouts, userId);
        });
    }

    @Test
    void givenValidInputs_whenGetWorkoutExerciseHistory_thenWorkoutsAreReturned() {
        UUID userId = UUID.randomUUID();
        long exerciseId = 1L;
        int previousWorkouts = 5;

        Mockito.when(userRepository.existsById(userId))
                .thenReturn(true);

        Mockito.when(exerciseRepository.existsInExercisesAccessibleByUser(exerciseId, userId))
                .thenReturn(true);

        workoutService.getWorkoutExerciseHistory(exerciseId, previousWorkouts, userId);

        Mockito.verify(workoutRepository).findLastWorkoutsContainingExercise(exerciseId, previousWorkouts, userId);
    }

}
