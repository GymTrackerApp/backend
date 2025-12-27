package com.gymtracker.app.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record WorkoutExerciseHistoryDTO(Long exerciseId, List<WorkoutSessionSnapshot> history) {

    @Builder
    public record WorkoutSessionSnapshot(Long workoutId, LocalDateTime workoutDate, List<SetDetail> sets) {

        @Builder
        public record SetDetail(int reps, double weight) {}
    }
}
