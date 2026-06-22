package com.example.personalgymapp.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.personalgymapp.model.Subscription

@Entity(
    tableName = "subscriptions",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clientId")]
)
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val clientId: Int,
    val clientName: String,
    val planName: String,
    val price: Double,
    val totalPaid: Double,
    val dueDate: String,
    val status: String
) {
    fun toDomainModel() = Subscription(
        id = id,
        clientId = clientId,
        clientName = clientName,
        planName = planName,
        price = price,
        totalPaid = totalPaid,
        dueDate = dueDate,
        status = status
    )

    companion object {
        fun fromDomainModel(subscription: Subscription) = SubscriptionEntity(
            id = subscription.id,
            clientId = subscription.clientId,
            clientName = subscription.clientName,
            planName = subscription.planName,
            price = subscription.price,
            totalPaid = subscription.totalPaid,
            dueDate = subscription.dueDate,
            status = subscription.status
        )
    }
}
