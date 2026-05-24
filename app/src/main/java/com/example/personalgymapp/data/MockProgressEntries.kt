package com.example.personalgymapp.data

import com.example.personalgymapp.model.ProgressEntry

val mockProgressEntries = listOf(
    ProgressEntry(
        id = 1,
        clientId = 1,
        clientName = "John Doe",
        date = "2024-05-01",
        weightKg = 85.5,
        bodyFatPercent = 22.0,
        chestCm = 105.0,
        waistCm = 92.0,
        hipsCm = 100.0,
        notes = "Starting point. Feeling motivated."
    ),
    ProgressEntry(
        id = 2,
        clientId = 1,
        clientName = "John Doe",
        date = "2024-05-15",
        weightKg = 84.2,
        bodyFatPercent = 21.5,
        chestCm = 104.5,
        waistCm = 90.5,
        hipsCm = 100.0,
        notes = "Good progress in two weeks. Strength increasing."
    ),
    ProgressEntry(
        id = 3,
        clientId = 2,
        clientName = "Jane Smith",
        date = "2024-05-05",
        weightKg = 62.0,
        bodyFatPercent = 18.5,
        chestCm = 88.0,
        waistCm = 68.0,
        hipsCm = 92.0,
        notes = "Initial measurements."
    ),
    ProgressEntry(
        id = 4,
        clientId = 2,
        clientName = "Jane Smith",
        date = "2024-05-20",
        weightKg = 61.5,
        bodyFatPercent = 18.0,
        chestCm = 88.5,
        waistCm = 67.5,
        hipsCm = 92.0,
        notes = "Consistency is key."
    ),
    ProgressEntry(
        id = 5,
        clientId = 1,
        clientName = "John Doe",
        date = "2024-06-01",
        weightKg = 83.0,
        bodyFatPercent = 20.8,
        chestCm = 104.0,
        waistCm = 89.0,
        hipsCm = 99.0,
        notes = "One month check-in. Excellent results."
    )
)
