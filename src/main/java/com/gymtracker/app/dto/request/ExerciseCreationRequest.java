package com.gymtracker.app.dto.request;

import com.gymtracker.app.domain.ExerciseCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record ExerciseCreationRequest(
        @NotBlank
        @Length(min = 2, max = 100, message = "Exercise name must be between 2 and 100 characters long")
        @Pattern(regexp = "[A-Za-z0-9-' ]+", message = "Exercise name can only contain letters, numbers and spaces")
        String name,
        ExerciseCategory category
) {}
