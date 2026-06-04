package com.example.personalgymapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "training_sessions")
data class TrainingSession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
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
