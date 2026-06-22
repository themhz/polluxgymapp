package com.example.personalgymapp.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.personalgymapp.model.Exercise
import com.example.personalgymapp.model.WorkoutPlan

@Entity(
    tableName = "workout_plan_exercises",
    primaryKeys = ["workoutPlanId", "exerciseId", "order"],
    foreignKeys = [
        ForeignKey(
            entity = WorkoutPlan::class,
            parentColumns = ["id"],
            childColumns = ["workoutPlanId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutPlanId"), Index("exerciseId")]
)
data class WorkoutPlanExerciseEntity(
    val workoutPlanId: Int,
    val exerciseId: Int,
    val order: Int,
    val sets: Int,
    val reps: Int,
    val restSeconds: Int,
    val exerciseType: String = "REPS",
    val targetDurationSeconds: Int? = null,
    val timerType: String = "COUNTDOWN",
    val isGpsEnabled: Boolean = false
)
