package com.example.personalgymapp.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscription_plans")
data class SubscriptionPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Double,
    val durationDays: Int,
    val description: String = ""
)
