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
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
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
    private final MessageSource messageSource;

    @Override
    @Transactional
    public void createWorkout(WorkoutCreationRequest request, UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserDoesNotExistException(
                    messageSource.getMessage("user-does-not-exist-exception.creating-workout", null, LocaleContextHolder.getLocale())
            );
        }

        if (request.trainingId() != null
                &&
            (!trainingPlanRepository.existsInUserAccessiblePlans(request.trainingId(), userId)
                    ||
            trainingPlanRepository.isDeleted(request.trainingId()))
        ) {
            throw new TrainingDoesNotExistException(
                    messageSource.getMessage("training-does-not-exist-exception.creating-workout", null, LocaleContextHolder.getLocale())
            );
        }

        int distinctExerciseCount = (int) request.workoutItems().stream()
                .map(WorkoutItemDTO::getExerciseId)
                .distinct()
                .count();

        if (distinctExerciseCount != request.workoutItems().size()) {
            throw new DuplicatedExercisesException(
                    messageSource.getMessage("duplicated-exercises-exception.creating-workout", null, LocaleContextHolder.getLocale())
            );
        }

        List<WorkoutItem> workoutItems = request.workoutItems().stream()
                .map(workoutItemDTO -> {
                    Exercise exercise = exerciseRepository.findExerciseAccessibleByUser(workoutItemDTO.getExerciseId(), userId)
                        .orElseThrow(() -> new ExerciseDoesNotExistException(
                                messageSource.getMessage("exercise-does-not-exist-exception.creating-workout", null, LocaleContextHolder.getLocale())
                        ));
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
            throw new UserDoesNotExistException(
                    messageSource.getMessage("user-does-not-exist-exception.getting-workouts-history", null, LocaleContextHolder.getLocale())
            );
        }

        if (!exerciseRepository.existsInExercisesAccessibleByUser(exerciseId, userId)) {
            throw new ExerciseDoesNotExistException(
                    messageSource.getMessage("exercise-does-not-exist-exception.getting-exercise-stats", null, LocaleContextHolder.getLocale())
            );
        }

        return workoutRepository.findLastWorkoutsContainingExercise(exerciseId, previousWorkouts, userId);
    }

    @Override
    public List<Workout> getWorkoutExerciseHistoryByWorkoutInPeriod(long exerciseId, LocalDate startDate, LocalDate endDate, UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserDoesNotExistException(
                    messageSource.getMessage("user-does-not-exist-exception.getting-workouts-history", null, LocaleContextHolder.getLocale())
            );
        }

        if (!exerciseRepository.existsInExercisesAccessibleByUser(exerciseId, userId)) {
            throw new ExerciseDoesNotExistException(
                    messageSource.getMessage("exercise-does-not-exist-exception.getting-exercise-stats", null, LocaleContextHolder.getLocale())
            );
        }

        if (!isPeriodValid(startDate, endDate)) {
            throw new InvalidPeriodException(
                    messageSource.getMessage("invalid-date-range", null, LocaleContextHolder.getLocale()));
        }

        return workoutRepository.findWorkoutsContainingExerciseInPeriod(exerciseId, startDate, endDate, userId);
    }

    @Override
    public List<Workout> getWorkoutTrainingHistory(long trainingId, LocalDate startDate, LocalDate endDate, UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserDoesNotExistException(
                    messageSource.getMessage("user-does-not-exist-exception.getting-workouts-history", null, LocaleContextHolder.getLocale())
            );
        }

        if (!trainingPlanRepository.existsInUserAccessiblePlans(trainingId, userId)
                ||
            trainingPlanRepository.isDeleted(trainingId)
        ) {
            throw new TrainingDoesNotExistException(
                    messageSource.getMessage("training-does-not-exist-exception.getting-workouts-history", null, LocaleContextHolder.getLocale())
            );
        }

        if (!isPeriodValid(startDate, endDate)) {
            throw new InvalidPeriodException(
                    messageSource.getMessage("invalid-date-range", null, LocaleContextHolder.getLocale())
            );
        }

        return workoutRepository.findWorkoutsByTrainingIdAndPeriod(trainingId, startDate, endDate, userId);
    }

    @Override
    public List<Workout> getWorkouts(Pageable pageable, LocalDate startDate, LocalDate endDate, Long trainingPlanId, UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserDoesNotExistException(
                    messageSource.getMessage("user-does-not-exist-exception.getting-workouts", null, LocaleContextHolder.getLocale())
            );
        }

        if (startDate != null && endDate != null && !isPeriodValid(startDate, endDate)) {
            throw new InvalidPeriodException(
                    messageSource.getMessage("invalid-date-range", null, LocaleContextHolder.getLocale())
            );
        }

        if (trainingPlanId != null && !trainingPlanRepository.existsInUserAccessiblePlans(trainingPlanId, userId)) {
            throw new TrainingDoesNotExistException(
                    messageSource.getMessage("training-does-not-exist-exception.getting-workouts", null, LocaleContextHolder.getLocale())
            );
        }

        return workoutRepository.findUserWorkouts(pageable, startDate, endDate, trainingPlanId, userId);
    }

    private boolean isPeriodValid(LocalDate startDate, LocalDate endDate) {
        return startDate != null && endDate != null && !startDate.isAfter(endDate);
    }
}
