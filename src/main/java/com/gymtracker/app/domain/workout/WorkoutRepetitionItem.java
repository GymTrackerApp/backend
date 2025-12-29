package com.gymtracker.app.domain.workout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WorkoutRepetitionItem extends WorkoutItem {
    private List<ExerciseSet> sets;

    public record ExerciseSet(int reps, BigDecimal weight) {
        public ExerciseSet {
            if (reps < 0) {
                throw new IllegalArgumentException("Repetitions must be greater than zero");
            }
            if (weight == null) {
                throw new IllegalArgumentException("Weight cannot be null");
            }
            if (weight.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Weight cannot be negative");
            }
        }
    }
}
