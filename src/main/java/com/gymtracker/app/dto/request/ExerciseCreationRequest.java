package com.gymtracker.app.dto.request;

import com.gymtracker.app.domain.ExerciseCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ExerciseCreationRequest(
        @NotBlank
        @Size(min = 2, max = 100, message = "Exercise name must be between 2 and 100 characters long")
        @Pattern(regexp = "[A-Za-z0-9-' ]+", message = "Exercise name can only contain letters, numbers and spaces")
        String name,

        @NotNull
        ExerciseCategory category
) {}
