package com.gymtracker.app.integration.workout;

import com.gymtracker.app.dto.request.WorkoutCreationRequest;
import com.gymtracker.app.dto.request.WorkoutItemDTO;
import com.gymtracker.app.dto.request.WorkoutRepetitionItemDTO;
import com.gymtracker.app.entity.ExerciseEntity;
import com.gymtracker.app.entity.TrainingPlanEntity;
import com.gymtracker.app.entity.UserEntity;
import com.gymtracker.app.entity.workout.WorkoutEntity;
import com.gymtracker.app.integration.BaseIntegrationTest;
import com.gymtracker.app.repository.jpa.exercise.SpringDataJpaExerciseRepository;
import com.gymtracker.app.repository.jpa.training.SpringDataJpaTrainingPlanRepository;
import com.gymtracker.app.repository.jpa.user.SpringDataJpaUserRepository;
import com.gymtracker.app.repository.jpa.workout.SpringDataWorkoutRepository;
import com.gymtracker.app.security.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WorkoutIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private SpringDataJpaUserRepository userRepository;

    @Autowired
    private SpringDataWorkoutRepository workoutRepository;

    @Autowired
    private SpringDataJpaExerciseRepository exerciseRepository;

    @Autowired
    private SpringDataJpaTrainingPlanRepository trainingPlanRepository;

    @Autowired
    private JwtService jwtService;

    @AfterEach
    void cleanup() {
        workoutRepository.deleteAll();
        trainingPlanRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(webTestClient);
    }

    @Test
    void givenValidWorkoutRequestWithTrainingId_whenCreateWorkout_thenWorkoutIsCreated() {
        UserEntity userEntity = UserEntity.builder()
                .username("testuser")
                .email("test@example.com")
                .passwordHash("hashedpassword")
                .build();

        userEntity = userRepository.save(userEntity);

        ExerciseEntity exercise1 = ExerciseEntity.builder()
                .name("Bench Press")
                .build();

        ExerciseEntity exercise2 = ExerciseEntity.builder()
                .name("Squat")
                .build();

        exercise1 = exerciseRepository.save(exercise1);
        exercise2 = exerciseRepository.save(exercise2);

        List<WorkoutRepetitionItemDTO.ExerciseSet> sets1 = List.of(
                WorkoutRepetitionItemDTO.ExerciseSet.builder()
                        .reps(10)
                        .weight(50.0)
                        .build(),
                WorkoutRepetitionItemDTO.ExerciseSet.builder()
                        .reps(8)
                        .weight(55.0)
                        .build()
        );

        List<WorkoutRepetitionItemDTO.ExerciseSet> sets2 = List.of(
                WorkoutRepetitionItemDTO.ExerciseSet.builder()
                        .reps(12)
                        .weight(60.0)
                        .build(),
                WorkoutRepetitionItemDTO.ExerciseSet.builder()
                        .reps(10)
                        .weight(65.0)
                        .build()
        );

        List<WorkoutItemDTO> workoutItems = List.of(
                WorkoutRepetitionItemDTO.builder()
                        .exerciseId(exercise1.getExerciseId())
                        .sets(sets1)
                        .build(),
                WorkoutRepetitionItemDTO.builder()
                        .exerciseId(exercise2.getExerciseId())
                        .sets(sets2)
                        .build()
        );

        TrainingPlanEntity trainingPlan = TrainingPlanEntity.builder()
                .name("Test Training Plan")
                .isCustom(false)
                .planItems(List.of())
                .build();

        trainingPlan = trainingPlanRepository.save(trainingPlan);

        WorkoutCreationRequest request = WorkoutCreationRequest.builder()
                .workoutItems(workoutItems)
                .trainingId(trainingPlan.getId())
                .build();

        String token = jwtService.generateToken(userEntity.getUsername(), userEntity.getUserId().toString());

        webTestClient.post()
                .uri("/workouts")
                .header("Authorization", "Bearer " + token)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated();

        var iterator = workoutRepository.findAll().iterator();
        Assertions.assertTrue(iterator.hasNext());

        WorkoutEntity createdWorkout = iterator.next();
        Assertions.assertEquals(userEntity.getUserId(), createdWorkout.getUser().getUserId());
        Assertions.assertEquals(trainingPlan.getId(), createdWorkout.getTraining().getId());
    }
}
