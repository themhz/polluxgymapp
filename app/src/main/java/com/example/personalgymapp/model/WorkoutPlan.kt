package com.example.personalgymapp.model

data class WorkoutPlan(
    val id: Int,
    val name: String,
    val notes: String,
    val exercises: List<WorkoutExercise>
)
