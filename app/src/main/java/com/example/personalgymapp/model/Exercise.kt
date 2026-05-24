package com.example.personalgymapp.model

data class Exercise(
    val id: Int,
    val name: String,
    val muscleGroup: String,
    val equipment: String,
    val difficulty: String,
    val instructions: String
)
