package com.example.personalgymapp.database.dao

import androidx.room.*
import com.example.personalgymapp.model.TrainingSession
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingSessionDao {
    @Query("SELECT * FROM training_sessions ORDER BY date DESC, time DESC")
    fun getAllSessions(): Flow<List<TrainingSession>>

    @Query("SELECT * FROM training_sessions WHERE clientId = :clientId")
    fun getSessionsForClient(clientId: Int): Flow<List<TrainingSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TrainingSession): Long

    @Update
    suspend fun updateSession(session: TrainingSession)

    @Delete
    suspend fun deleteSession(session: TrainingSession)

    @Query("DELETE FROM training_sessions")
    suspend fun clearTable()
}
