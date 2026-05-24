package com.example.personalgymapp.model

data class WorkoutExercise(
    val exerciseId: Int,
    val exerciseName: String,
    val sets: Int,
    val reps: Int,
    val restSeconds: Int,
    val exerciseType: String = "REPS",
    val targetDurationSeconds: Int? = null
)
