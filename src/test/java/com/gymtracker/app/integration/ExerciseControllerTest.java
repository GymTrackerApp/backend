package com.gymtracker.app.integration;

import com.gymtracker.app.controller.ExerciseController;
import com.gymtracker.app.dto.request.ExerciseCreationRequest;
import com.gymtracker.app.security.JwtAuthenticationFilter;
import com.gymtracker.app.service.ExerciseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebMvcTest(controllers = ExerciseController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class ExerciseControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ExerciseService exerciseService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(webTestClient);
    }

    @Test
    void givenExerciseData_whenCreateCustomExerciseCalled_shouldReturnCreatedResponse() {
        ExerciseCreationRequest exerciseCreationRequest = ExerciseCreationRequest.builder()
                .name("My new exercise")
                .build();

        webTestClient.post()
                .uri("/exercises")
                .bodyValue(exerciseCreationRequest)
                .exchange()
                .expectStatus()
                .isCreated();
    }
}
