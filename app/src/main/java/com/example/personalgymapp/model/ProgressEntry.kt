package com.example.personalgymapp.model

data class ProgressEntry(
    val id: Int,
    val clientId: Int,
    val clientName: String,
    val date: String,
    val weightKg: Double,
    val bodyFatPercent: Double?,
    val chestCm: Double?,
    val waistCm: Double?,
    val hipsCm: Double?,
    val notes: String
)
