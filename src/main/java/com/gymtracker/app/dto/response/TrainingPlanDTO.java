package com.gymtracker.app.dto.response;

import java.util.List;

public record TrainingPlanDTO(Long id, String name, List<PlanItemDTO> planItems) {
    public record PlanItemDTO(Long exerciseId, String exerciseName) { }
}
