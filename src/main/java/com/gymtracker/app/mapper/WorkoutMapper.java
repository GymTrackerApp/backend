package com.gymtracker.app.mapper;

import com.gymtracker.app.domain.workout.Workout;
import com.gymtracker.app.domain.workout.WorkoutItem;
import com.gymtracker.app.domain.workout.WorkoutRepetitionItem;
import com.gymtracker.app.dto.request.WorkoutCreationRequest;
import com.gymtracker.app.dto.response.WorkoutExerciseHistoryDTO;
import com.gymtracker.app.entity.workout.WorkoutEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = WorkoutItemMapper.class)
public interface WorkoutMapper {
    @Mapping(target = "user.userId", source = "userId")
    @Mapping(target = "training.id", source = "trainingId")
    WorkoutEntity workoutToWorkoutEntity(Workout workout);

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "trainingId", source = "training.id")
    Workout workoutEntityToWorkout(WorkoutEntity workoutEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "userId", ignore = true)
    Workout workoutCreationRequestToWorkout(WorkoutCreationRequest workoutCreationRequest);

    default WorkoutExerciseHistoryDTO toWorkoutExerciseHistoryDTO(Long exerciseId, List<Workout> workouts) {
        return WorkoutExerciseHistoryDTO.builder()
                .exerciseId(exerciseId)
                .history(workoutsToWorkoutSessionSnapshots(workouts, exerciseId))
                .build();
    }

    default List<WorkoutExerciseHistoryDTO.WorkoutSessionSnapshot> workoutsToWorkoutSessionSnapshots(List<Workout> workouts, Long exerciseId) {
        return workouts.stream()
                .map(workout -> WorkoutExerciseHistoryDTO.WorkoutSessionSnapshot.builder()
                        .workoutId(workout.getId())
                        .workoutDate(workout.getCreatedAt())
                        .sets(extractSetsForExercise(workout, exerciseId))
                        .build())
                .toList();
    }

    default List<WorkoutExerciseHistoryDTO.WorkoutSessionSnapshot.SetDetail> extractSetsForExercise(Workout workout, Long exerciseId) {
        return workout.getWorkoutItems().stream()
                .filter(workoutItem -> workoutItem.getExercise().getExerciseId().equals(exerciseId))
                .filter(WorkoutRepetitionItem.class::isInstance)
                .flatMap(workoutItem -> ((WorkoutRepetitionItem) workoutItem).getSets().stream())
                .map(exerciseSet -> WorkoutExerciseHistoryDTO.WorkoutSessionSnapshot.SetDetail.builder()
                        .reps(exerciseSet.reps())
                        .weight(exerciseSet.weight())
                        .build())
                .toList();
    }
}
