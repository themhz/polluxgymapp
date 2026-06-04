package com.example.personalgymapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.personalgymapp.database.converters.Converters
import com.example.personalgymapp.database.dao.*
import com.example.personalgymapp.database.entity.*
import com.example.personalgymapp.model.*

@Database(
    entities = [
        ClientEntity::class, 
        SubscriptionEntity::class, 
        PaymentEntity::class,
        Exercise::class,
        WorkoutPlan::class,
        TrainingSession::class,
        SessionExerciseResult::class
    ], 
    version = 5, 
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun paymentDao(): PaymentDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutPlanDao(): WorkoutPlanDao
    abstract fun trainingSessionDao(): TrainingSessionDao
    abstract fun sessionResultDao(): SessionExerciseResultDao

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
