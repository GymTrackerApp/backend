package com.gymtracker.app.repository.jpa.exercise;

import com.gymtracker.app.entity.ExerciseEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface SpringDataJpaExerciseRepository extends CrudRepository<ExerciseEntity, Long> {
    boolean existsByNameAndOwnerUserId(String name, UUID ownerId);
    boolean existsByNameAndOwnerIsNull(String name);
    Set<ExerciseEntity> findAllByOwnerIsNull();

    @Query("""
        SELECT e FROM ExerciseEntity e
        WHERE e.exerciseId = :exerciseId
        AND (e.owner.userId = :ownerId OR e.isCustom = false)
        """)
    Optional<ExerciseEntity> findExerciseAccessibleByUser(Long exerciseId, UUID ownerId);
}
