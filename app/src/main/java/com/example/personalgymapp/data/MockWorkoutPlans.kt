package com.example.personalgymapp.data

import com.example.personalgymapp.model.WorkoutExercise
import com.example.personalgymapp.model.WorkoutPlan

val mockWorkoutPlans = listOf(
    WorkoutPlan(
        id = 1,
        name = "Full Body Beginner",
        clientId = 1,
        clientName = "John Doe",
        notes = "Focus on form and consistent tempo.",
        exercises = listOf(
            WorkoutExercise(1, "Bench Press", 3, 10, 60, "REPS", null),
            WorkoutExercise(5, "Squats", 3, 12, 90, "REPS", null),
            WorkoutExercise(11, "Plank", 3, 0, 60, "TIME", 60),
            WorkoutExercise(13, "Wall Sit", 2, 0, 60, "TIME", 45)
        )
    ),
    WorkoutPlan(
        id = 2,
        name = "Upper Body Power",
        clientId = 2,
        clientName = "Jane Smith",
        notes = "Heavy weights, focus on explosive concentric phase.",
        exercises = listOf(
            WorkoutExercise(1, "Bench Press", 4, 6, 120, "REPS", null),
            WorkoutExercise(4, "Bent Over Row", 4, 6, 120, "REPS", null),
            WorkoutExercise(7, "Overhead Press", 3, 8, 90, "REPS", null),
            WorkoutExercise(9, "Bicep Curls", 3, 10, 60, "REPS", null),
            WorkoutExercise(10, "Tricep Dips", 3, 12, 60, "REPS", null)
        )
    ),
    WorkoutPlan(
        id = 3,
        name = "High Intensity Cardio",
        clientId = 6,
        clientName = "Themis",
        notes = "Focus on steady jumping rhythm.",
        exercises = listOf(
            WorkoutExercise(14, "Jumping Rope", 3, 0, 60, "TIME", 180)
        )
    )
)
