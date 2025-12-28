package com.gymtracker.app.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private List<PlanItem> planItems;
    private boolean isCustom;

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanItem {
        private Exercise exercise;
        private int defaultSets;
    }
}
