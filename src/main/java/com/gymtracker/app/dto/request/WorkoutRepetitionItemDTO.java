package com.gymtracker.app.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
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
    @NotNull(message = "Sets information should be provided")
    @Size(min = 1, max = MAX_SETS, message = "Sets amount must be between {min} and {max} for each exercise")
    private List<ExerciseSet> sets;

    @Builder
    public record ExerciseSet(
            @NotNull(message = "Sets amount must be provided")
            @PositiveOrZero(message = "Reps cannot be negative")
            @Max(value = MAX_REPS, message = "Reps amount cannot exceed {value}")
            int reps,

            @NotNull(message = "Weight must be provided")
            @PositiveOrZero(message = "Weight cannot be negative")
            @Max(value = MAX_WEIGHT, message = "Weight cannot exceed {value} kg")
            BigDecimal weight) {}
}
