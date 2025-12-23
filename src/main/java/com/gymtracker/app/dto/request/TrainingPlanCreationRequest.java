package com.gymtracker.app.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record TrainingPlanCreationRequest(
        @NotBlank(message = "Plan name must not be blank")
        String planName,

        @Valid
        @NotNull(message = "Plan items must not be null")
        @Size(min = 0, max = 30, message = "Plan exercises amount must be between 0 and 30")
        List<PlanItem> planItems
) {

    @Builder
    public record PlanItem(
            @NotNull(message = "Exercise id must not be null")
            Long exerciseId,

            @Min(value = 0, message = "Default sets must be at least 0")
            @Max(value = 50, message = "Default sets must be at most 50")
            int defaultSets
    ) { }
}
