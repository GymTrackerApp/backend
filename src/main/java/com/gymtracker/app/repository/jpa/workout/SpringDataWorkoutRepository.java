package com.gymtracker.app.repository.jpa.workout;

import com.gymtracker.app.entity.workout.WorkoutEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

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
}
