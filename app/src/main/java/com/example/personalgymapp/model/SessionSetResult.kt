package com.example.personalgymapp.model

data class SessionSetResult(
    val setNumber: Int,
    val reps: Int?,
    val weightKg: Double?,
    val durationSeconds: Int?,
    val notes: String
)
