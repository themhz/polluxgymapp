package com.example.personalgymapp.model

data class Payment(
    val id: Int = 0,
    val clientId: Int,
    val amount: Double,
    val date: String,
    val notes: String = ""
)
