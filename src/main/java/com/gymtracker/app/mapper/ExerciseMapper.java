package com.gymtracker.app.mapper;

import com.gymtracker.app.dto.request.ExerciseCreationRequest;
import com.gymtracker.app.dto.response.UserExerciseDTO;
import com.gymtracker.app.entity.Exercise;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExerciseMapper {
    Exercise exerciseCreationRequestToExercise(ExerciseCreationRequest exerciseCreationRequest);
    UserExerciseDTO exerciseToUserExerciseDTO(Exercise savedExercise);
}
