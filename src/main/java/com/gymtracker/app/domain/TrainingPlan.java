package com.gymtracker.app.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingPlan {
    private Long id;
    private String name;
    private UUID ownerId;
    private List<TrainingPlanItem> planItems;
    private boolean isCustom;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrainingPlanItem {
        private Exercise exercise;
        private int defaultSets;
    }
}
