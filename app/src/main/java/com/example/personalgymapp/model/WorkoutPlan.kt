package com.example.personalgymapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_plans")
data class WorkoutPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val notes: String,
    val exercises: List<WorkoutExercise>
)
