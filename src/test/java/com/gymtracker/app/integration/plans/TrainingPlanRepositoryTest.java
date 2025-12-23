package com.gymtracker.app.integration.plans;

import com.gymtracker.app.entity.ExerciseEntity;
import com.gymtracker.app.entity.PlanItemEntity;
import com.gymtracker.app.entity.TrainingPlanEntity;
import com.gymtracker.app.integration.BaseIntegrationTest;
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

    @Test
    void givenPredefinedTrainingPlan_whenGetAllPredefinedPlansCalled_shouldReturnThePredefinedPlan() {
        ExerciseEntity exerciseEntity = ExerciseEntity.builder().build();
        exerciseRepository.save(exerciseEntity);

        TrainingPlanEntity trainingPlanEntity = TrainingPlanEntity.builder()
                .name("Predefined Training Plan")
                .isCustom(false)
                .planItems(List.of(PlanItemEntity.builder().exercise(exerciseEntity).defaultSets(3).build()))
                .build();
        trainingPlanRepository.save(trainingPlanEntity);

        List<TrainingPlanEntity> predefinedPlans = trainingPlanRepository.findAllByIsCustomFalse();

        Assertions.assertFalse(predefinedPlans.isEmpty());
        Assertions.assertEquals("Predefined Training Plan", predefinedPlans.getFirst().getName());
    }

    @Test
    void givenCustomTrainingPlan_whenGetAllPredefinedPlansCalled_shouldNotReturnTheCustomPlan() {
        ExerciseEntity exerciseEntity = ExerciseEntity.builder().build();
        exerciseRepository.save(exerciseEntity);

        TrainingPlanEntity trainingPlanEntity = TrainingPlanEntity.builder()
                .name("Custom Training Plan")
                .isCustom(true)
                .planItems(List.of(PlanItemEntity.builder().exercise(exerciseEntity).defaultSets(3).build()))
                .build();
        trainingPlanRepository.save(trainingPlanEntity);

        List<TrainingPlanEntity> predefinedPlans = trainingPlanRepository.findAllByIsCustomFalse();
        Assertions.assertTrue(predefinedPlans.isEmpty());
    }

}
