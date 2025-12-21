package com.gymtracker.app.repository.jpa;

import com.gymtracker.app.entity.ExerciseEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface SpringDataJpaExerciseRepository extends CrudRepository<ExerciseEntity, Long> {
    boolean existsByNameAndOwnerUserId(String name, UUID ownerId);
    boolean existsByNameAndOwnerIsNull(String name);
    Set<ExerciseEntity> findAllByOwnerIsNull();
}
