package com.example.personalgymapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.personalgymapp.database.dao.ClientDao
import com.example.personalgymapp.database.dao.PaymentDao
import com.example.personalgymapp.database.dao.SubscriptionDao
import com.example.personalgymapp.database.entity.ClientEntity
import com.example.personalgymapp.database.entity.PaymentEntity
import com.example.personalgymapp.database.entity.SubscriptionEntity

@Database(entities = [ClientEntity::class, SubscriptionEntity::class, PaymentEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun paymentDao(): PaymentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "personal_trainer_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
