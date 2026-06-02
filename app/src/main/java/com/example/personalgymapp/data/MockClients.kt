package com.example.personalgymapp.data

import com.example.personalgymapp.database.entity.ClientEntity

val mockClients = listOf(
    ClientEntity(
        id = 1,
        name = "John Doe",
        goal = "Weight Loss",
        birthDate = "1992-05-15",
        phone = "555-0101",
        email = "john.doe@example.com",
        sessionsCompleted = 12,
        nextSession = "2026-03-24"
    ),
    ClientEntity(
        id = 2,
        name = "Jane Smith",
        goal = "Muscle Gain",
        birthDate = "1996-11-20",
        phone = "555-0102",
        email = "jane.smith@example.com",
        sessionsCompleted = 8,
        nextSession = "2026-03-25"
    ),
    ClientEntity(
        id = 3,
        name = "Mike Johnson",
        goal = "Endurance Training",
        birthDate = "1979-02-10",
        phone = "555-0103",
        email = "mike.j@example.com",
        sessionsCompleted = 20,
        nextSession = "2026-03-26"
    ),
    ClientEntity(
        id = 4,
        name = "Sarah Wilson",
        goal = "General Fitness",
        birthDate = "1989-08-05",
        phone = "555-0104",
        email = "s.wilson@example.com",
        sessionsCompleted = 5,
        nextSession = "2026-03-27"
    ),
    ClientEntity(
        id = 5,
        name = "David Brown",
        goal = "Strength Training",
        birthDate = "1984-12-30",
        phone = "555-0105",
        email = "d.brown@example.com",
        sessionsCompleted = 15,
        nextSession = "2026-03-28"
    ),
    ClientEntity(
        id = 6,
        name = "Themis",
        goal = "Cardio & Stamina",
        birthDate = "1994-04-12",
        phone = "555-0106",
        email = "themis@example.com",
        sessionsCompleted = 0,
        nextSession = "2026-03-29"
    )
)
