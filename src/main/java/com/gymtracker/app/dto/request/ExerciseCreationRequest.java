package com.gymtracker.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ExerciseCreationRequest(
        @NotBlank
        String name) {
}
