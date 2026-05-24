package com.example.personalgymapp.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val goal: String,
    val age: Int,
    val phone: String,
    val email: String,
    val sessionsCompleted: Int,
    val nextSession: String
)
