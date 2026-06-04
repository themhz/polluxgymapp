package com.example.personalgymapp.repository

import com.example.personalgymapp.database.dao.*
import com.example.personalgymapp.database.entity.*
import com.example.personalgymapp.model.*
import kotlinx.coroutines.flow.Flow

class ClientRepository(
    private val clientDao: ClientDao,
    private val subscriptionDao: SubscriptionDao,
    private val paymentDao: PaymentDao,
    private val exerciseDao: ExerciseDao,
    private val workoutPlanDao: WorkoutPlanDao,
    private val trainingSessionDao: TrainingSessionDao,
    private val sessionResultDao: SessionExerciseResultDao
) {
    val allClients: Flow<List<ClientEntity>> = clientDao.getAllClients()
    val allSubscriptions: Flow<List<SubscriptionEntity>> = subscriptionDao.getAllSubscriptions()
    val allExercises: Flow<List<Exercise>> = exerciseDao.getAllExercises()
    val allWorkoutPlans: Flow<List<WorkoutPlan>> = workoutPlanDao.getAllWorkoutPlans()
    val allSessions: Flow<List<TrainingSession>> = trainingSessionDao.getAllSessions()

    fun getClientById(id: Int): Flow<ClientEntity?> = clientDao.getClientById(id)
    
    fun getSubscriptionsForClient(clientId: Int): Flow<List<SubscriptionEntity>> = 
        subscriptionDao.getSubscriptionsForClient(clientId)

    fun getPaymentsForClient(clientId: Int): Flow<List<PaymentEntity>> =
        paymentDao.getPaymentsForClient(clientId)

    fun getSessionsForClient(clientId: Int): Flow<List<TrainingSession>> =
        trainingSessionDao.getSessionsForClient(clientId)

    fun getResultsForSession(sessionId: Int): Flow<List<SessionExerciseResult>> =
        sessionResultDao.getResultsForSession(sessionId)

    // Clients
    suspend fun insertClient(client: ClientEntity) = clientDao.insertClient(client)
    suspend fun updateClient(client: ClientEntity) = clientDao.updateClient(client)
    suspend fun deleteClient(client: ClientEntity) = clientDao.deleteClient(client)

    // Subscriptions
    suspend fun insertSubscription(subscription: SubscriptionEntity) = subscriptionDao.insertSubscription(subscription)
    suspend fun updateSubscription(subscription: SubscriptionEntity) = subscriptionDao.updateSubscription(subscription)
    suspend fun deleteSubscription(subscription: SubscriptionEntity) = subscriptionDao.deleteSubscription(subscription)

    // Payments
    suspend fun insertPayment(payment: PaymentEntity) = paymentDao.insertPayment(payment)
    suspend fun updatePayment(payment: PaymentEntity) = paymentDao.updatePayment(payment)
    suspend fun deletePayment(payment: PaymentEntity) = paymentDao.deletePayment(payment)

    // Exercises
    suspend fun insertExercise(exercise: Exercise) = exerciseDao.insertExercise(exercise)
    suspend fun updateExercise(exercise: Exercise) = exerciseDao.updateExercise(exercise)
    suspend fun deleteExercise(exercise: Exercise) = exerciseDao.deleteExercise(exercise)

    // Workout Plans
    suspend fun insertWorkoutPlan(plan: WorkoutPlan) = workoutPlanDao.insertWorkoutPlan(plan)
    suspend fun updateWorkoutPlan(plan: WorkoutPlan) = workoutPlanDao.updateWorkoutPlan(plan)
    suspend fun deleteWorkoutPlan(plan: WorkoutPlan) = workoutPlanDao.deleteWorkoutPlan(plan)

    // Training Sessions
    suspend fun insertSession(session: TrainingSession) = trainingSessionDao.insertSession(session)
    suspend fun updateSession(session: TrainingSession) = trainingSessionDao.updateSession(session)
    suspend fun deleteSession(session: TrainingSession) = trainingSessionDao.deleteSession(session)

    // Session Results
    suspend fun insertSessionResult(result: SessionExerciseResult) = sessionResultDao.insertResult(result)
}
