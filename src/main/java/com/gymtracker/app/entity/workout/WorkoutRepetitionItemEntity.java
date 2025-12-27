package com.gymtracker.app.entity.workout;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("REPS")
public class WorkoutRepetitionItemEntity extends WorkoutItemEntity {
    @ElementCollection
    @CollectionTable(name = "repetition_exercise_sets", joinColumns = @JoinColumn(name = "workout_item_id", referencedColumnName = "id"))
    @OrderColumn(name = "set_order")
    private List<ExerciseSet> sets;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExerciseSet {
        private int reps;
        private double weight;
    }
}
