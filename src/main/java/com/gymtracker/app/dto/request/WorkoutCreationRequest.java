package com.gymtracker.app.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
public record WorkoutCreationRequest(
        Long trainingId,

        @Valid
        @NotEmpty(message = "Cannot create a workout without exercises")
        List<WorkoutItemDTO> workoutItems) {
}
