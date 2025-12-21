package com.gymtracker.app.integration;

import com.gymtracker.app.dto.request.ExerciseCreationRequest;
import com.gymtracker.app.entity.ExerciseEntity;
import com.gymtracker.app.entity.UserEntity;
import com.gymtracker.app.repository.jpa.SpringDataJpaExerciseRepository;
import com.gymtracker.app.repository.jpa.SpringDataJpaUserRepository;
import com.gymtracker.app.security.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private SpringDataJpaExerciseRepository exerciseRepository;

    @Autowired
    private SpringDataJpaUserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        UserEntity user = UserEntity.builder()
                .username("testuser")
                .passwordHash(passwordEncoder.encode("testpassword123@"))
                .build();

        UserEntity savedUser = userRepository.save(user);

        String jwt = jwtService.generateToken(savedUser.getUsername(), savedUser.getUserId().toString());

        webTestClient.post()
                .uri("/exercises")
                .header("Authorization", "Bearer " + jwt)
                .bodyValue(exerciseCreationRequest)
                .exchange()
                .expectStatus()
                .isCreated();

        ExerciseEntity exercise = exerciseRepository.findAll().iterator().next();
        Assertions.assertEquals(1, exerciseRepository.count());
        Assertions.assertEquals(exerciseCreationRequest.name(), exercise.getName());
        Assertions.assertEquals(savedUser.getUserId(), exercise.getOwner().getUserId());
    }
}
