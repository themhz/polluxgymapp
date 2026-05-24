package com.example.personalgymapp.model

data class TrainingSession(
    val id: Int,
    val clientId: Int,
    val clientName: String,
    val date: String,
    val time: String,
    val durationMinutes: Int,
    val sessionType: String,
    val status: String,
    val notes: String,
    val workoutPlanId: Int? = null
)
