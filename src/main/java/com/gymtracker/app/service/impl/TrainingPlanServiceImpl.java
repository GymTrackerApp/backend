package com.gymtracker.app.service.impl;

import com.gymtracker.app.domain.Exercise;
import com.gymtracker.app.domain.TrainingPlan;
import com.gymtracker.app.domain.User;
import com.gymtracker.app.dto.request.TrainingPlanCreationRequest;
import com.gymtracker.app.exception.ExerciseDoesNotExistException;
import com.gymtracker.app.exception.UserDoesNotExistException;
import com.gymtracker.app.repository.ExerciseRepository;
import com.gymtracker.app.repository.TrainingPlanRepository;
import com.gymtracker.app.repository.UserRepository;
import com.gymtracker.app.service.TrainingPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingPlanServiceImpl implements TrainingPlanService {
    private final UserRepository userRepository;
    private final TrainingPlanRepository trainingPlanRepository;
    private final ExerciseRepository exerciseRepository;

    @Override
    public TrainingPlan generateCustomTrainingPlan(TrainingPlanCreationRequest request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("Cannot create training plan for non-existing user"));

        List<TrainingPlan.TrainingPlanItem> trainingPlanItems = request.planItems()
                .stream()
                .map(planItem -> {
                    Exercise exercise = exerciseRepository.findExerciseAccessibleByUser(planItem.exerciseId(), userId)
                        .orElseThrow(() -> new ExerciseDoesNotExistException("Exercise with id " + planItem.exerciseId() + " does not exist"));

                    return new TrainingPlan.TrainingPlanItem(exercise, planItem.defaultSets());
                })
                .toList();

        TrainingPlan trainingPlan = user.createCustomTrainingPlan(request.planName(), trainingPlanItems);
        return trainingPlanRepository.save(trainingPlan);
    }
}
