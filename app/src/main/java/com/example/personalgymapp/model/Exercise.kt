package com.example.personalgymapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
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
