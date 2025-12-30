package com.gymtracker.app.service.impl;

import com.gymtracker.app.domain.Exercise;
import com.gymtracker.app.domain.workout.Workout;
import com.gymtracker.app.domain.workout.WorkoutItem;
import com.gymtracker.app.dto.request.WorkoutCreationRequest;
import com.gymtracker.app.dto.request.WorkoutItemDTO;
import com.gymtracker.app.exception.DuplicatedExercisesException;
import com.gymtracker.app.exception.ExerciseDoesNotExistException;
import com.gymtracker.app.exception.InvalidPeriodException;
import com.gymtracker.app.exception.TrainingDoesNotExistException;
import com.gymtracker.app.exception.UserDoesNotExistException;
import com.gymtracker.app.mapper.WorkoutItemMapper;
import com.gymtracker.app.repository.ExerciseRepository;
import com.gymtracker.app.repository.TrainingPlanRepository;
import com.gymtracker.app.repository.UserRepository;
import com.gymtracker.app.repository.WorkoutRepository;
import com.gymtracker.app.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {
    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final TrainingPlanRepository trainingPlanRepository;

    private final WorkoutItemMapper workoutItemMapper;

    @Override
    @Transactional
    public void createWorkout(WorkoutCreationRequest request, UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserDoesNotExistException("Cannot create workout for non-existing user");
        }

        if (request.trainingId() != null && !trainingPlanRepository.existsInUserAccessiblePlans(request.trainingId(), userId)) {
            throw new TrainingDoesNotExistException("Cannot create workout for non-accessible training plan");
        }

        int distinctExerciseCount = (int) request.workoutItems().stream()
                .map(WorkoutItemDTO::getExerciseId)
                .distinct()
                .count();

        if (distinctExerciseCount != request.workoutItems().size()) {
            throw new DuplicatedExercisesException("Cannot create workout with duplicated exercises");
        }

        List<WorkoutItem> workoutItems = request.workoutItems().stream()
                .map(workoutItemDTO -> {
                    Exercise exercise = exerciseRepository.findExerciseAccessibleByUser(workoutItemDTO.getExerciseId(), userId)
                        .orElseThrow(() -> new ExerciseDoesNotExistException("Cannot create workout with non-existing exercise"));
                    WorkoutItem workoutItem = workoutItemMapper.workoutItemDTOToWorkoutItem(workoutItemDTO);
                    workoutItem.setExercise(exercise);
                    return workoutItem;
                })
                .toList();

        Workout workout = Workout.create(userId, request.trainingId(), workoutItems);
        workoutRepository.save(workout);
    }

    @Override
    public List<Workout> getWorkoutExerciseHistory(long exerciseId, int previousWorkouts, UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserDoesNotExistException("Cannot get workout history for non-existing user");
        }

        if (!exerciseRepository.existsInExercisesAccessibleByUser(exerciseId, userId)) {
            throw new ExerciseDoesNotExistException("Cannot get exercise stats for non-existing exercise");
        }

        return workoutRepository.findLastWorkoutsContainingExercise(exerciseId, previousWorkouts, userId);
    }

    @Override
    public List<Workout> getWorkoutExerciseHistoryByWorkoutInPeriod(long exerciseId, LocalDate startDate, LocalDate endDate, UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserDoesNotExistException("Cannot get workout history for non-existing user");
        }

        if (!exerciseRepository.existsInExercisesAccessibleByUser(exerciseId, userId)) {
            throw new ExerciseDoesNotExistException("Cannot get exercise stats for non-existing exercise");
        }

        if (!isPeriodValid(startDate, endDate)) {
            throw new InvalidPeriodException("Invalid date range provided");
        }

        return workoutRepository.findWorkoutsContainingExerciseInPeriod(exerciseId, startDate, endDate, userId);
    }

    @Override
    public List<Workout> getWorkoutTrainingHistory(long trainingId, LocalDate startDate, LocalDate endDate, UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserDoesNotExistException("Cannot get workout history for non-existing user");
        }

        if (!trainingPlanRepository.existsInUserAccessiblePlans(trainingId, userId)) {
            throw new TrainingDoesNotExistException("Cannot get workout history for non-existing training plan");
        }

        if (!isPeriodValid(startDate, endDate)) {
            throw new InvalidPeriodException("Invalid date range provided");
        }

        return workoutRepository.findWorkoutsByTrainingIdAndPeriod(trainingId, startDate, endDate, userId);
    }

    private boolean isPeriodValid(LocalDate startDate, LocalDate endDate) {
        return startDate != null && endDate != null && !startDate.isAfter(endDate);
    }
}
