package com.example.personalgymapp.database.dao

import androidx.room.*
import com.example.personalgymapp.database.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments WHERE clientId = :clientId ORDER BY date DESC")
    fun getPaymentsForClient(clientId: Int): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    @Delete
    suspend fun deletePayment(payment: PaymentEntity)

    @Query("DELETE FROM payments")
    suspend fun clearTable()
}
