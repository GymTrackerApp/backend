package com.gymtracker.app.controller;

import com.gymtracker.app.dto.request.ExerciseCreationRequest;
import com.gymtracker.app.entity.User;
import com.gymtracker.app.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ExerciseController {
    private final ExerciseService exerciseService;

    @PostMapping("/exercises")
    public ResponseEntity<Void> createCustomExercise(@RequestBody ExerciseCreationRequest exerciseCreationRequest, @AuthenticationPrincipal User owner) {
        exerciseService.createExercise(exerciseCreationRequest, owner);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
