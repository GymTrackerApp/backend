package com.gymtracker.app.repository;

import com.gymtracker.app.entity.workout.WorkoutEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WorkoutSpecifications {
    private WorkoutSpecifications() {}

    public static Specification<WorkoutEntity> filterWorkouts(Long trainingPlanId, LocalDate startDate, LocalDate endDate) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (trainingPlanId != null) {
                predicates.add(criteriaBuilder.equal(root.get("training").get("id"), trainingPlanId));
            }

            if (startDate != null && endDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
