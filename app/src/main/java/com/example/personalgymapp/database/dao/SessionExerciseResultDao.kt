package com.example.personalgymapp.database.dao

import androidx.room.*
import com.example.personalgymapp.model.SessionExerciseResult
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionExerciseResultDao {
    @Query("SELECT * FROM session_exercise_results WHERE trainingSessionId = :sessionId")
    fun getResultsForSession(sessionId: Int): Flow<List<SessionExerciseResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: SessionExerciseResult)

    @Delete
    suspend fun deleteResult(result: SessionExerciseResult)

    @Query("DELETE FROM session_exercise_results")
    suspend fun clearTable()
}
