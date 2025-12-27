package com.gymtracker.app.controller;

import com.gymtracker.app.domain.workout.Workout;
import com.gymtracker.app.dto.request.WorkoutCreationRequest;
import com.gymtracker.app.dto.response.MessageResponse;
import com.gymtracker.app.dto.response.WorkoutExerciseHistoryDTO;
import com.gymtracker.app.mapper.WorkoutMapper;
import com.gymtracker.app.service.WorkoutService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workouts")
@RequiredArgsConstructor
public class WorkoutController {
    private final WorkoutService workoutService;
    private final WorkoutMapper workoutMapper;

    @PostMapping
    public ResponseEntity<MessageResponse> createWorkout(
            @Valid @RequestBody WorkoutCreationRequest workoutCreationRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        workoutService.createWorkout(workoutCreationRequest, UUID.fromString(userDetails.getUsername()));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Workout created successfully"));
    }

    @GetMapping("/exercises/{exerciseId}/history")
    public ResponseEntity<WorkoutExerciseHistoryDTO> getWorkoutExerciseHistory(
            @PathVariable("exerciseId") long exerciseId,

            @Valid
            @Positive(message = "Previous workouts parameter must be positive")
            @Max(value = 10, message = "Previous workouts parameter must not exceed 10")
            @RequestParam(name = "previousWorkouts", defaultValue = "3") int previousWorkouts,

            @AuthenticationPrincipal UserDetails userDetails) {
        List<Workout> lastWorkoutsContainingExercise = workoutService.getWorkoutExerciseHistory(
                exerciseId,
                previousWorkouts,
                UUID.fromString(userDetails.getUsername())
        );

        WorkoutExerciseHistoryDTO workoutExerciseHistoryDTO = workoutMapper.toWorkoutExerciseHistoryDTO(exerciseId, lastWorkoutsContainingExercise);
        return ResponseEntity.status(HttpStatus.OK)
                .body(workoutExerciseHistoryDTO);
    }
}
