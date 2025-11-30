package com.gymtracker.app.integration;

import com.gymtracker.app.dto.request.ExerciseCreationRequest;
import com.gymtracker.app.entity.Exercise;
import com.gymtracker.app.entity.User;
import com.gymtracker.app.repository.ExerciseRepository;
import com.gymtracker.app.repository.UserRepository;
import com.gymtracker.app.security.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class ExerciseTest {
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:18.1-alpine");

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @AfterEach
    void cleanUp() {
        exerciseRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DynamicPropertySource
    public static void dynamicPropertyConfig(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void givenNewExercise_whenCreateExercise_thenExerciseSavedInDatabase() {
        ExerciseCreationRequest exerciseCreationRequest = ExerciseCreationRequest.builder()
                .name("My new exercise")
                .build();

        User user = User.builder()
                .username("testuser")
                .build();

        User savedUser = userRepository.save(user);

        String jwt = jwtService.generateToken(savedUser.getDisplayUsername(), savedUser.getUsername());

        webTestClient.post()
                .uri("/exercises")
                .header("Authorization", "Bearer " + jwt)
                .bodyValue(exerciseCreationRequest)
                .exchange()
                .expectStatus()
                .isCreated();

        Exercise exercise = exerciseRepository.findAll().iterator().next();
        Assertions.assertEquals(1, exerciseRepository.count());
        Assertions.assertEquals(exerciseCreationRequest.name(), exercise.getName());
        Assertions.assertEquals(savedUser.getUserId(), exercise.getOwner().getUserId());
    }
}
