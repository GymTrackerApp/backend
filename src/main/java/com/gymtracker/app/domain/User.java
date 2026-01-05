package com.gymtracker.app.domain;

import com.gymtracker.app.exception.DuplicatedExercisesException;
import com.gymtracker.app.exception.ExerciseAlreadyExistsException;
import com.gymtracker.app.exception.ExerciseDoesNotExistException;
import com.gymtracker.app.exception.PlanWithSameNameAlreadyExistsException;
import com.gymtracker.app.exception.TrainingDoesNotExistException;
import com.gymtracker.app.exception.TrainingPlansAmountExceededException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
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
    private Instant createdAt;

    @Setter(AccessLevel.NONE)
    private Password password;
    private Set<Exercise> exercises;
    private List<TrainingPlan> plans;

    public void updatePassword(String passwordHash) {
        this.password = new Password(passwordHash);
    }

    public Exercise createCustomExercise(String name, ExerciseCategory category) {
        validateDuplicatedExerciseName(name);

        Exercise newExercise = Exercise.builder()
                .name(name)
                .isCustom(true)
                .ownerId(this.userId)
                .category(category)
                .build();

        exercises.add(newExercise);

        return newExercise;
    }

    public Exercise updateCustomExercise(long existingExerciseId, String newExerciseName, ExerciseCategory exerciseCategory) {
        Exercise exercise = exercises.stream()
                .filter(ex -> ex.getExerciseId().equals(existingExerciseId))
                .findFirst()
                .orElseThrow(() -> new ExerciseDoesNotExistException("Cannot update non-existing exercise"));

        exercises.remove(exercise);

        validateDuplicatedExerciseName(newExerciseName);

        exercise.setName(newExerciseName);
        exercise.setCategory(exerciseCategory);
        exercises.add(exercise);

        return exercise;
    }

    private void validateDuplicatedExerciseName(String name) {
        if (exercises.stream().anyMatch(exercise -> exercise.getName().equals(name))) {
            throw new ExerciseAlreadyExistsException("Exercise with the same name already exists in your exercises");
        }
    }

    public void removeCustomExercise(long exerciseId) {
        if (exercises.stream().noneMatch(exercise -> exercise.getExerciseId().equals(exerciseId))) {
            throw new ExerciseDoesNotExistException("Cannot delete non-existing exercise");
        }

        exercises.removeIf(exercise -> exercise.getExerciseId().equals(exerciseId));
    }

    public TrainingPlan createCustomTrainingPlan(String planName, List<TrainingPlan.PlanItem> trainingPlanItems) {
        validateDuplicatedExercises(trainingPlanItems);

        if (plans.size() >= MAX_TRAINING_PLANS_PER_USER) {
            throw new TrainingPlansAmountExceededException("User cannot have more than " + MAX_TRAINING_PLANS_PER_USER + " training plans");
        }

        validateDuplicatedPlanName(planName);

        TrainingPlan newPlan = TrainingPlan.builder()
                .name(planName)
                .ownerId(this.userId)
                .isCustom(true)
                .planItems(trainingPlanItems)
                .build();

        plans.add(newPlan);

        return newPlan;
    }

    public TrainingPlan updateCustomTrainingPlan(long existingPlanId, String newName, List<TrainingPlan.PlanItem> newPlanItems) {
        TrainingPlan trainingPlan = plans.stream()
                .filter(plan -> plan.getId().equals(existingPlanId))
                .findFirst()
                .orElseThrow(() -> new TrainingDoesNotExistException("Cannot update non-existing training plan"));

        plans.remove(trainingPlan);

        validateDuplicatedPlanName(newName);
        validateDuplicatedExercises(newPlanItems);

        trainingPlan.setName(newName);
        trainingPlan.setPlanItems(newPlanItems);
        plans.add(trainingPlan);

        return trainingPlan;
    }

    private void validateDuplicatedPlanName(String planName) {
        boolean planWithSameNameExists = plans.stream()
                .anyMatch(trainingPlan -> trainingPlan.getName().equals(planName));

        if (planWithSameNameExists) {
            throw new PlanWithSameNameAlreadyExistsException("User already has a training plan with the same name");
        }
    }

    private void validateDuplicatedExercises(List<TrainingPlan.PlanItem> trainingPlanItems) {
        long distinctExercisesCount = trainingPlanItems.stream()
                .map(TrainingPlan.PlanItem::getExercise)
                .distinct()
                .count();

        if (distinctExercisesCount != trainingPlanItems.size()) {
            throw new DuplicatedExercisesException("Training plan items contain duplicated exercises");
        }
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
