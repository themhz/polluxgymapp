package com.example.personalgymapp.database.dao

import androidx.room.*
import com.example.personalgymapp.database.entity.WorkoutPlanExerciseEntity
import com.example.personalgymapp.database.entity.WorkoutPlanWithExercises
import com.example.personalgymapp.model.WorkoutPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutPlanDao {
    @Transaction
    @Query("SELECT * FROM workout_plans ORDER BY name ASC")
    fun getAllWorkoutPlansWithExercises(): Flow<List<WorkoutPlanWithExercises>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutPlan(plan: WorkoutPlan): Long

    @Update
    suspend fun updateWorkoutPlan(plan: WorkoutPlan)

    @Delete
    suspend fun deleteWorkoutPlan(plan: WorkoutPlan)

    @Query("DELETE FROM workout_plans")
    suspend fun clearTable()

    // Workout Plan Exercises
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanExercise(planExercise: WorkoutPlanExerciseEntity)

    @Query("DELETE FROM workout_plan_exercises WHERE workoutPlanId = :planId")
    suspend fun deleteExercisesForPlan(planId: Int)

    @Query("SELECT * FROM workout_plan_exercises WHERE workoutPlanId = :planId ORDER BY `order` ASC")
    suspend fun getExercisesForPlan(planId: Int): List<WorkoutPlanExerciseEntity>

    @Transaction
    suspend fun insertWorkoutPlanWithExercises(plan: WorkoutPlan) {
        val planId = insertWorkoutPlan(plan).toInt()
        plan.exercises.forEachIndexed { index, ex ->
            insertPlanExercise(
                WorkoutPlanExerciseEntity(
                    workoutPlanId = planId,
                    exerciseId = ex.exerciseId,
                    order = index,
                    sets = ex.sets,
                    reps = ex.reps,
                    restSeconds = ex.restSeconds,
                    exerciseType = ex.exerciseType,
                    targetDurationSeconds = ex.targetDurationSeconds,
                    timerType = ex.timerType,
                    isGpsEnabled = ex.isGpsEnabled
                )
            )
        }
    }

    @Transaction
    suspend fun updateWorkoutPlanWithExercises(plan: WorkoutPlan) {
        updateWorkoutPlan(plan)
        deleteExercisesForPlan(plan.id)
        plan.exercises.forEachIndexed { index, ex ->
            insertPlanExercise(
                WorkoutPlanExerciseEntity(
                    workoutPlanId = plan.id,
                    exerciseId = ex.exerciseId,
                    order = index,
                    sets = ex.sets,
                    reps = ex.reps,
                    restSeconds = ex.restSeconds,
                    exerciseType = ex.exerciseType,
                    targetDurationSeconds = ex.targetDurationSeconds,
                    timerType = ex.timerType,
                    isGpsEnabled = ex.isGpsEnabled
                )
            )
        }
    }
}
