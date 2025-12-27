package com.gymtracker.app.repository.jpa.workout;

import com.gymtracker.app.domain.workout.Workout;
import com.gymtracker.app.entity.workout.WorkoutEntity;
import com.gymtracker.app.mapper.WorkoutMapper;
import com.gymtracker.app.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WorkoutRepositoryAdapter implements WorkoutRepository {
    private final SpringDataWorkoutRepository workoutRepository;
    private final WorkoutMapper workoutMapper;

    @Override
    public Workout save(Workout workout) {
        WorkoutEntity workoutEntity = workoutMapper.workoutToWorkoutEntity(workout);
        WorkoutEntity savedWorkoutEntity = workoutRepository.save(workoutEntity);
        return workoutMapper.workoutEntityToWorkout(savedWorkoutEntity);
    }

    @Override
    public List<Workout> findLastWorkoutsContainingExercise(long exerciseId, int previousWorkouts, UUID userId) {
        Pageable lastN = PageRequest.of(0, previousWorkouts);

        List<WorkoutEntity> workoutEntities = workoutRepository.findLastWorkoutsContainingExercise(exerciseId, userId, lastN);
        return workoutEntities.stream()
                .map(workoutMapper::workoutEntityToWorkout)
                .toList();
    }
}
