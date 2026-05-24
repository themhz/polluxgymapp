package com.example.personalgymapp.model

data class SessionExerciseResult(
    val id: Int,
    val trainingSessionId: Int,
    val exerciseId: Int,
    val exerciseName: String,
    val sets: List<SessionSetResult>,
    val notes: String
)
