package com.example.personalgymapp.model

data class Subscription(
    val id: Int,
    val clientId: Int,
    val clientName: String,
    val planName: String, // e.g., "12 Sessions", "Monthly Unlimited"
    val price: Double,
    val totalPaid: Double,
    val dueDate: String,
    val status: String // "Paid", "Pending", "Overdue"
) {
    val balance: Double get() = price - totalPaid
}
