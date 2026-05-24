package com.example.personalgymapp.data

import com.example.personalgymapp.model.TrainingSession

val mockTrainingSessions = listOf(
    TrainingSession(
        id = 1,
        clientId = 1,
        clientName = "John Doe",
        date = "2024-06-10",
        time = "10:00 AM",
        durationMinutes = 60,
        sessionType = "Strength Training",
        status = "Scheduled",
        notes = "Focus on leg press and squats today.",
        workoutPlanId = 1
    ),
    TrainingSession(
        id = 2,
        clientId = 2,
        clientName = "Jane Smith",
        date = "2024-06-10",
        time = "02:30 PM",
        durationMinutes = 45,
        sessionType = "Weight Loss",
        status = "Completed",
        notes = "High intensity interval training.",
        workoutPlanId = 2
    ),
    TrainingSession(
        id = 3,
        clientId = 3,
        clientName = "Mike Johnson",
        date = "2024-06-11",
        time = "08:00 AM",
        durationMinutes = 60,
        sessionType = "Mobility",
        status = "Scheduled",
        notes = "Stretching and flexibility focus.",
        workoutPlanId = null
    ),
    TrainingSession(
        id = 4,
        clientId = 4,
        clientName = "Sarah Wilson",
        date = "2024-06-11",
        time = "05:00 PM",
        durationMinutes = 60,
        sessionType = "Assessment",
        status = "Cancelled",
        notes = "Client had a last-minute meeting conflict.",
        workoutPlanId = null
    ),
    TrainingSession(
        id = 5,
        clientId = 5,
        clientName = "David Brown",
        date = "2024-06-12",
        time = "11:30 AM",
        durationMinutes = 75,
        sessionType = "Conditioning",
        status = "Scheduled",
        notes = "Endurance focused session.",
        workoutPlanId = null
    ),
    TrainingSession(
        id = 6,
        clientId = 1,
        clientName = "John Doe",
        date = "2024-06-13",
        time = "09:00 AM",
        durationMinutes = 60,
        sessionType = "Strength Training",
        status = "Scheduled",
        notes = "Upper body push focus.",
        workoutPlanId = 1
    )
)
