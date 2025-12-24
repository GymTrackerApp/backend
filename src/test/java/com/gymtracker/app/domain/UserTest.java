package com.gymtracker.app.domain;

import com.gymtracker.app.exception.PlanWithSameNameAlreadyExistsException;
import com.gymtracker.app.exception.TrainingPlansAmountExceededException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void givenExceedingMaxPlans_whenCreateCustomTrainingPlan_thenThrowException() {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .plans(new ArrayList<>())
                .build();

        for (int i = 0; i < 5; i++) {
            user.getPlans().add(TrainingPlan.builder().name("Plan " + i).build());
        }

        assertThrows(TrainingPlansAmountExceededException.class, () -> {
            user.createCustomTrainingPlan("New Plan", new ArrayList<>());
        });
    }

    @Test
    void givenDuplicatePlanName_whenCreateCustomTrainingPlan_thenThrowException() {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .plans(new ArrayList<>())
                .build();
        user.getPlans().add(TrainingPlan.builder().name("Duplicate Plan").build());

        assertThrows(PlanWithSameNameAlreadyExistsException.class, () -> {
            user.createCustomTrainingPlan("Duplicate Plan", new ArrayList<>());
        });
    }

    @Test
    void givenValidInput_whenCreateCustomTrainingPlan_thenCreatePlanSuccessfully() {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .plans(new ArrayList<>())
                .build();

        TrainingPlan newPlan = user.createCustomTrainingPlan("Unique Plan", new ArrayList<>());

        assertNotNull(newPlan);
        assertEquals("Unique Plan", newPlan.getName());
        assertTrue(user.getPlans().contains(newPlan));
    }
}