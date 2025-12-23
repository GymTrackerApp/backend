package com.gymtracker.app.repository.jpa.training;

import com.gymtracker.app.entity.TrainingPlanEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SpringDataJpaTrainingPlanRepository extends CrudRepository<TrainingPlanEntity, Long> {
    List<TrainingPlanEntity> findAllByIsCustomFalse();
}
