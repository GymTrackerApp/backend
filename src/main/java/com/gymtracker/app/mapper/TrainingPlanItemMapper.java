package com.gymtracker.app.mapper;

import com.gymtracker.app.domain.TrainingPlan;
import com.gymtracker.app.entity.PlanItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = ExerciseMapper.class)
public interface TrainingPlanItemMapper {
    PlanItemEntity trainingPlanItemToPlanItemEntity(TrainingPlan.TrainingPlanItem trainingPlanItem);
    TrainingPlan.TrainingPlanItem planItemEntityToTrainingPlanItem(PlanItemEntity planItemEntity);
}
