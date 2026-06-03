package com.example.personalgymapp.model

data class Exercise(
    val id: Int,
    val name: String,
    val muscleGroup: String,
    val equipment: String,
    val difficulty: String,
    val instructions: String,
    val focusArea: String = "Upper Body", // Full Body, Upper Body, Lower Body
    val trainingType: String = "Resistance", // Resistance, Cardio, Mobility
    val imageResName: String? = null,
    val videoResName: String? = null,
    val imageUri: String? = null,
    val videoUri: String? = null
)
