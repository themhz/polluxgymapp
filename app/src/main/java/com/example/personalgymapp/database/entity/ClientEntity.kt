package com.example.personalgymapp.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "clients",
    foreignKeys = [
        ForeignKey(
            entity = SubscriptionPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["subscriptionPlanId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("subscriptionPlanId")]
)
data class ClientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val goal: String,
    val birthDate: Date,
    val phone: String,
    val email: String,
    val sessionsCompleted: Int,
    val nextSession: String,
    val subscriptionPlanId: Int? = null
)
