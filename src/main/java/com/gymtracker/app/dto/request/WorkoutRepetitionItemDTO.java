package com.gymtracker.app.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@Jacksonized
public class WorkoutRepetitionItemDTO extends WorkoutItemDTO {
    private static final int MAX_SETS = 100;
    private static final int MAX_REPS = 300;
    private static final int MAX_WEIGHT = 600;

    @Valid
    @Size(max = MAX_SETS, message = "Number of sets cannot exceed " + MAX_SETS)
    private List<ExerciseSet> sets;

    @Builder
    public record ExerciseSet(
            @PositiveOrZero(message = "Reps cannot be negative")
            @Max(value = MAX_REPS, message = "Reps amount cannot exceed " + MAX_REPS)
            int reps,

            @PositiveOrZero(message = "Weight cannot be negative")
            @Max(value = MAX_WEIGHT, message = "Weight cannot exceed " + MAX_WEIGHT + " kg")
            double weight) {}
}
