package com.example.personalgymapp.data

import com.example.personalgymapp.database.entity.ClientEntity

val mockClients = listOf(
    ClientEntity(
        id = 1,
        name = "John Doe",
        goal = "Weight Loss",
        age = 32,
        phone = "555-0101",
        email = "john.doe@example.com",
        sessionsCompleted = 12,
        nextSession = "Monday, 10:00 AM"
    ),
    ClientEntity(
        id = 2,
        name = "Jane Smith",
        goal = "Muscle Gain",
        age = 28,
        phone = "555-0102",
        email = "jane.smith@example.com",
        sessionsCompleted = 8,
        nextSession = "Tuesday, 02:30 PM"
    ),
    ClientEntity(
        id = 3,
        name = "Mike Johnson",
        goal = "Endurance Training",
        age = 45,
        phone = "555-0103",
        email = "mike.j@example.com",
        sessionsCompleted = 20,
        nextSession = "Wednesday, 08:00 AM"
    ),
    ClientEntity(
        id = 4,
        name = "Sarah Wilson",
        goal = "General Fitness",
        age = 35,
        phone = "555-0104",
        email = "s.wilson@example.com",
        sessionsCompleted = 5,
        nextSession = "Thursday, 05:00 PM"
    ),
    ClientEntity(
        id = 5,
        name = "David Brown",
        goal = "Strength Training",
        age = 40,
        phone = "555-0105",
        email = "d.brown@example.com",
        sessionsCompleted = 15,
        nextSession = "Friday, 11:30 AM"
    )
)
