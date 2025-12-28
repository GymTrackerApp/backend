package com.gymtracker.app.repository;

import com.gymtracker.app.domain.TrainingPlan;

import java.util.List;
import java.util.UUID;

public interface TrainingPlanRepository {
    TrainingPlan save(TrainingPlan trainingPlan);
    List<TrainingPlan> findAllPredefinedPlans();
    boolean existsInUserAccessiblePlans(Long trainingId, UUID userId);
}
