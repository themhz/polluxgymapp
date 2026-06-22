package com.example.personalgymapp.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "workout_plans")
data class WorkoutPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val notes: String,
    @Ignore
    val exercises: List<WorkoutExercise> = emptyList()
) {
    // Empty constructor for Room
    constructor(id: Int, name: String, notes: String) : this(id, name, notes, emptyList())
}
