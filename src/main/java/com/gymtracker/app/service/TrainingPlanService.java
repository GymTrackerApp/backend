package com.gymtracker.app.service;

import com.gymtracker.app.domain.TrainingPlan;
import com.gymtracker.app.dto.request.TrainingPlanCreationRequest;

import java.util.List;
import java.util.UUID;

public interface TrainingPlanService {
    TrainingPlan generateCustomTrainingPlan(TrainingPlanCreationRequest request, UUID userId);
    List<TrainingPlan> getAllPredefinedTrainingPlans();
    List<TrainingPlan> getUserTrainingPlans(UUID ownerId);
    TrainingPlan getTrainingPlanById(long trainingPlanId, UUID userId);
}
