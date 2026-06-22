package com.example.personalgymapp.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "session_exercise_results",
    foreignKeys = [
        ForeignKey(
            entity = TrainingSession::class,
            parentColumns = ["id"],
            childColumns = ["trainingSessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("trainingSessionId"), Index("exerciseId")]
)
data class SessionExerciseResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val trainingSessionId: Int,
    val exerciseId: Int,
    val exerciseName: String,
    val sets: List<SessionSetResult>,
    val notes: String,
    val gpsPath: List<GPSPoint>? = null
)

data class GPSPoint(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)
