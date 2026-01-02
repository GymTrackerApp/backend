package com.gymtracker.app.repository.jpa.workout;

import com.gymtracker.app.entity.UserEntity;
import com.gymtracker.app.entity.workout.WorkoutEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SpringDataWorkoutRepository extends CrudRepository<WorkoutEntity, Long> {
    @Query("""
        SELECT w FROM WorkoutEntity w
        JOIN w.workoutItems item
        WHERE w.user.userId = :userId
        AND item.exercise.exerciseId = :exerciseId
        ORDER BY w.createdAt DESC
    """)
    List<WorkoutEntity> findLastWorkoutsContainingExercise(Long exerciseId, UUID userId, Pageable pageable);

    @Query("""
        SELECT w FROM WorkoutEntity w
        JOIN w.workoutItems item
        WHERE w.user.userId = :userId
        AND item.exercise.exerciseId = :exerciseId
        AND w.createdAt BETWEEN :startDate AND :endDate
        ORDER BY w.createdAt DESC
    """)
    List<WorkoutEntity> findWorkoutsContainingExerciseInPeriod(Long exerciseId, LocalDate startDate, LocalDate endDate, UUID userId);

    List<WorkoutEntity> findWorkoutEntitiesByTraining_IdAndCreatedAtBetweenAndUser_UserId(Long trainingId, LocalDate startDate, LocalDate endDate, UUID userId);

    List<WorkoutEntity> findWorkoutsByUser_UserId(UUID userId, Pageable pageable);
    List<WorkoutEntity> findWorkoutsByUser_UserIdAndCreatedAtBetween(UUID userId, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
