package com.gymtracker.app.controller;

import com.gymtracker.app.domain.Exercise;
import com.gymtracker.app.dto.request.ExerciseCreationRequest;
import com.gymtracker.app.dto.response.UserExerciseDTO;
import com.gymtracker.app.mapper.ExerciseMapper;
import com.gymtracker.app.service.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
public class ExerciseController {
    private final ExerciseService exerciseService;
    private final ExerciseMapper exerciseMapper;

    @PostMapping
    public ResponseEntity<UserExerciseDTO> createCustomExercise(
            @Valid @RequestBody ExerciseCreationRequest exerciseCreationRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        Exercise exercise = exerciseMapper.exerciseCreationRequestToExercise(exerciseCreationRequest);
        Exercise createdExercise = exerciseService.createCustomExercise(exercise, UUID.fromString(userDetails.getUsername()));
        UserExerciseDTO userExerciseDTO = exerciseMapper.exerciseToUserExerciseDTO(createdExercise);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userExerciseDTO);
    }
}
