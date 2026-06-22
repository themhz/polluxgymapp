package com.example.personalgymapp.database.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.example.personalgymapp.model.WorkoutPlan

data class WorkoutPlanWithExercises(
    @Embedded val plan: WorkoutPlan,
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutPlanId"
    )
    val workoutPlanExercises: List<WorkoutPlanExerciseEntity>
)
