package com.example.personalgymapp.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.personalgymapp.model.Payment

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val clientId: Int,
    val amount: Double,
    val date: String,
    val notes: String = ""
) {
    fun toDomainModel() = Payment(
        id = id,
        clientId = clientId,
        amount = amount,
        date = date,
        notes = notes
    )

    companion object {
        fun fromDomainModel(payment: Payment) = PaymentEntity(
            id = payment.id,
            clientId = payment.clientId,
            amount = payment.amount,
            date = payment.date,
            notes = payment.notes
        )
    }
}
