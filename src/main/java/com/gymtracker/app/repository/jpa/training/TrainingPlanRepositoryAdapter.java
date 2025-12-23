package com.gymtracker.app.repository.jpa.training;

import com.gymtracker.app.domain.TrainingPlan;
import com.gymtracker.app.entity.TrainingPlanEntity;
import com.gymtracker.app.mapper.TrainingPlanMapper;
import com.gymtracker.app.repository.TrainingPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TrainingPlanRepositoryAdapter implements TrainingPlanRepository {
    private final SpringDataJpaTrainingPlanRepository repository;
    private final TrainingPlanMapper trainingPlanMapper;

    @Override
    public TrainingPlan save(TrainingPlan trainingPlan) {
        TrainingPlanEntity trainingPlanEntity = trainingPlanMapper.trainingPlanToTrainingPlanEntity(trainingPlan);
        TrainingPlanEntity savedTrainingPlanEntity = repository.save(trainingPlanEntity);
        return trainingPlanMapper.trainingPlanEntityToTrainingPlan(savedTrainingPlanEntity);
    }
}
