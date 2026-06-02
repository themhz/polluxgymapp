package com.example.personalgymapp.data

import com.example.personalgymapp.model.Subscription

val mockSubscriptions = listOf(
    Subscription(
        id = 1,
        clientId = 1,
        clientName = "John Doe",
        planName = "12 Session Pack",
        price = 360.0,
        totalPaid = 360.0,
        dueDate = "2024-05-20",
        status = "Paid"
    ),
    Subscription(
        id = 2,
        clientId = 2,
        clientName = "Jane Smith",
        planName = "Monthly Unlimited",
        price = 150.0,
        totalPaid = 100.0,
        dueDate = "2024-06-01",
        status = "Pending"
    ),
    Subscription(
        id = 3,
        clientId = 3,
        clientName = "Michael Scott",
        planName = "8 Session Pack",
        price = 280.0,
        totalPaid = 0.0,
        dueDate = "2024-05-10",
        status = "Overdue"
    ),
    Subscription(
        id = 4,
        clientId = 6,
        clientName = "Themis",
        planName = "Annual Gold",
        price = 1200.0,
        totalPaid = 1200.0,
        dueDate = "2025-01-01",
        status = "Paid"
    )
)
