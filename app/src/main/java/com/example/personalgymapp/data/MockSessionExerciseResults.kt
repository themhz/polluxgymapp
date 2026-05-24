package com.example.personalgymapp.data

import com.example.personalgymapp.model.SessionExerciseResult
import com.example.personalgymapp.model.SessionSetResult

val mockSessionExerciseResults = listOf(
    // Session 1: John Doe - Strength Training
    SessionExerciseResult(
        id = 1,
        trainingSessionId = 1,
        exerciseId = 1,
        exerciseName = "Bench Press",
        sets = listOf(
            SessionSetResult(1, 10, 50.0, null, ""),
            SessionSetResult(2, 9, 50.0, null, ""),
            SessionSetResult(3, 8, 47.5, null, "Lowered weight for last set")
        ),
        notes = "Good form throughout"
    ),
    SessionExerciseResult(
        id = 2,
        trainingSessionId = 1,
        exerciseId = 5,
        exerciseName = "Squats",
        sets = listOf(
            SessionSetResult(1, 12, 60.0, null, ""),
            SessionSetResult(2, 12, 60.0, null, ""),
            SessionSetResult(3, 10, 60.0, null, "")
        ),
        notes = ""
    ),
    // Session 2: Jane Smith - Weight Loss
    SessionExerciseResult(
        id = 3,
        trainingSessionId = 2,
        exerciseId = 2,
        exerciseName = "Push-ups",
        sets = listOf(
            SessionSetResult(1, 15, null, null, ""),
            SessionSetResult(2, 12, null, null, ""),
            SessionSetResult(3, 10, null, null, "")
        ),
        notes = "Bodyweight only"
    ),
    SessionExerciseResult(
        id = 4,
        trainingSessionId = 2,
        exerciseId = 11,
        exerciseName = "Plank",
        sets = listOf(
            SessionSetResult(1, null, null, 60, ""),
            SessionSetResult(2, null, null, 45, ""),
            SessionSetResult(3, null, null, 30, "")
        ),
        notes = "Focus on core stability"
    )
)
