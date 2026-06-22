package com.example.personalgymapp.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.personalgymapp.database.entity.ClientEntity

@Entity(
    tableName = "training_sessions",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WorkoutPlan::class,
            parentColumns = ["id"],
            childColumns = ["workoutPlanId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("clientId"), Index("workoutPlanId")]
)
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
