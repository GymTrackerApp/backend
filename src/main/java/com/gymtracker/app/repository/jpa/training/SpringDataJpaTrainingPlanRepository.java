package com.gymtracker.app.repository.jpa.training;

import com.gymtracker.app.entity.TrainingPlanEntity;
import org.springframework.data.repository.CrudRepository;

public interface SpringDataJpaTrainingPlanRepository extends CrudRepository<TrainingPlanEntity, Long> {
}
