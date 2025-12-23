package com.gymtracker.app.repository;

import com.gymtracker.app.domain.TrainingPlan;

import java.util.List;

public interface TrainingPlanRepository {
    TrainingPlan save(TrainingPlan trainingPlan);
    List<TrainingPlan> findAllPredefinedPlans();
}
