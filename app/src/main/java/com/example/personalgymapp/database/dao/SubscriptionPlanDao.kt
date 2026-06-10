package com.example.personalgymapp.database.dao

import androidx.room.*
import com.example.personalgymapp.database.entity.SubscriptionPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionPlanDao {
    @Query("SELECT * FROM subscription_plans")
    fun getAllSubscriptionPlans(): Flow<List<SubscriptionPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscriptionPlan(plan: SubscriptionPlanEntity)

    @Update
    suspend fun updateSubscriptionPlan(plan: SubscriptionPlanEntity)

    @Delete
    suspend fun deleteSubscriptionPlan(plan: SubscriptionPlanEntity)

    @Query("DELETE FROM subscription_plans")
    suspend fun clearTable()
}
