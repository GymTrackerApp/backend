package com.gymtracker.app.domain;

import com.gymtracker.app.exception.PlanWithSameNameAlreadyExistsException;
import com.gymtracker.app.exception.TrainingPlansAmountExceededException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class User implements UserDetails {
    private static final int MAX_TRAINING_PLANS_PER_USER = 5;

    private final UUID userId;
    private String username;
    private String email;

    @Setter(AccessLevel.NONE)
    private Password password;
    private Set<Exercise> exercises;
    private List<TrainingPlan> plans;

    public void updatePassword(String passwordHash) {
        this.password = new Password(passwordHash);
    }

    public Exercise createCustomExercise(String name, ExerciseCategory category) {
        Exercise newExercise = Exercise.builder()
                .name(name)
                .isCustom(true)
                .ownerId(this.userId)
                .category(category)
                .build();

        exercises.add(newExercise);

        return newExercise;
    }

    public TrainingPlan createCustomTrainingPlan(String planName, List<TrainingPlan.PlanItem> trainingPlanItems) {
        if (plans.size() >= MAX_TRAINING_PLANS_PER_USER) {
            throw new TrainingPlansAmountExceededException("User cannot have more than " + MAX_TRAINING_PLANS_PER_USER + " training plans");
        }

        boolean planWithSameNameExists = plans.stream()
                .anyMatch(trainingPlan -> trainingPlan.getName().equals(planName));

        if (planWithSameNameExists) {
            throw new PlanWithSameNameAlreadyExistsException("User already has a training plan with the same name");
        }

        TrainingPlan newPlan = TrainingPlan.builder()
                .name(planName)
                .ownerId(this.userId)
                .isCustom(true)
                .planItems(trainingPlanItems)
                .build();

        plans.add(newPlan);

        return newPlan;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password.hashedPassword();
    }

    @Override
    public String getUsername() {
        return userId.toString();
    }

    public String getDisplayUsername() {
        return username;
    }
}
