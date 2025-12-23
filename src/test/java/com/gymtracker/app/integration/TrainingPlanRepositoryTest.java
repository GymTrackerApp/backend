package com.gymtracker.app.integration;

import com.gymtracker.app.entity.ExerciseEntity;
import com.gymtracker.app.entity.PlanItemEntity;
import com.gymtracker.app.entity.TrainingPlanEntity;
import com.gymtracker.app.repository.jpa.exercise.SpringDataJpaExerciseRepository;
import com.gymtracker.app.repository.jpa.training.SpringDataJpaTrainingPlanRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
class TrainingPlanRepositoryTest extends BaseIntegrationTest {
    @Autowired
    private SpringDataJpaTrainingPlanRepository trainingPlanRepository;

    @Autowired
    private SpringDataJpaExerciseRepository exerciseRepository;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(trainingPlanRepository);
    }

    @Test
    void givenValidData_whenSaveMethodCalled_shouldSaveTrainingPlan() {
        TrainingPlanEntity trainingPlanEntity = TrainingPlanEntity.builder()
                .name("My Training Plan")
                .isCustom(false)
                .planItems(List.of(PlanItemEntity.builder().exercise(ExerciseEntity.builder().build()).defaultSets(3).build()))
                .build();

        TrainingPlanEntity savedTrainingPlanEntity = trainingPlanRepository.save(trainingPlanEntity);

        Assertions.assertNotNull(savedTrainingPlanEntity);
        Assertions.assertEquals(trainingPlanEntity.getName(), savedTrainingPlanEntity.getName());
        Assertions.assertNotNull(savedTrainingPlanEntity.getId());
    }

    @Test
    void givenValidData_whenGetMethodCalled_shouldGetAllTrainingPlans() {
        ExerciseEntity exerciseEntity = ExerciseEntity.builder().build();
        exerciseRepository.save(exerciseEntity);

        TrainingPlanEntity trainingPlanEntity = TrainingPlanEntity.builder()
                .name("My Training Plan")
                .isCustom(false)
                .planItems(List.of(PlanItemEntity.builder().exercise(exerciseEntity).defaultSets(3).build()))
                .build();

        trainingPlanRepository.save(trainingPlanEntity);

        Iterable<TrainingPlanEntity> trainingPlanEntities = trainingPlanRepository.findAll();
        Assertions.assertNotNull(trainingPlanEntities.iterator().next());
    }

}
