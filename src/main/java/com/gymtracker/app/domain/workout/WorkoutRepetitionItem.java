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

    public record ExerciseSet(int reps, BigDecimal weight) { }
}
