package com.example.personalgymapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalgymapp.database.entity.*
import com.example.personalgymapp.model.*
import com.example.personalgymapp.repository.ClientRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ClientViewModel(private val repository: ClientRepository) : ViewModel() {

    val clients: StateFlow<List<ClientEntity>> = repository.allClients
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val subscriptions: StateFlow<List<Subscription>> = repository.allSubscriptions
        .map { entities -> entities.map { it.toDomainModel() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val exercises: StateFlow<List<Exercise>> = repository.allExercises
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val workoutPlans: StateFlow<List<WorkoutPlan>> = repository.allWorkoutPlans
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val trainingSessions: StateFlow<List<TrainingSession>> = repository.allSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getPaymentsForClient(clientId: Int): Flow<List<Payment>> =
        repository.getPaymentsForClient(clientId).map { entities ->
            entities.map { it.toDomainModel() }
        }

    fun addPayment(payment: Payment) {
        viewModelScope.launch {
            repository.insertPayment(PaymentEntity.fromDomainModel(payment))
            
            // Update totalPaid in subscription
            val clientSubs = repository.getSubscriptionsForClient(payment.clientId).first()
            val activeSub = clientSubs.find { it.status != "Paid" } ?: clientSubs.lastOrNull()
            
            if (activeSub != null) {
                val updatedSub = activeSub.copy(
                    totalPaid = activeSub.totalPaid + payment.amount,
                    status = if (activeSub.totalPaid + payment.amount >= activeSub.price) "Paid" else activeSub.status
                )
                repository.updateSubscription(updatedSub)
            }
        }
    }

    fun seedDatabaseIfEmpty() {
        // Mock data seeding removed
    }

    // Exercise Methods
    fun addExercise(exercise: Exercise) = viewModelScope.launch { repository.insertExercise(exercise) }
    fun updateExercise(exercise: Exercise) = viewModelScope.launch { repository.updateExercise(exercise) }
    fun deleteExercise(exercise: Exercise) = viewModelScope.launch { repository.deleteExercise(exercise) }

    // Workout Plan Methods
    fun addWorkoutPlan(plan: WorkoutPlan) = viewModelScope.launch { repository.insertWorkoutPlan(plan) }
    fun updateWorkoutPlan(plan: WorkoutPlan) = viewModelScope.launch { repository.updateWorkoutPlan(plan) }
    fun deleteWorkoutPlan(plan: WorkoutPlan) = viewModelScope.launch { repository.deleteWorkoutPlan(plan) }

    // Training Session Methods
    fun addSession(session: TrainingSession) = viewModelScope.launch { repository.insertSession(session) }
    fun updateSession(session: TrainingSession) = viewModelScope.launch { repository.updateSession(session) }
    fun deleteSession(session: TrainingSession) = viewModelScope.launch { repository.deleteSession(session) }
    fun getSessionsForClient(clientId: Int) = repository.getSessionsForClient(clientId)

    // Session Result Methods
    fun addSessionResult(result: SessionExerciseResult) = viewModelScope.launch { repository.insertSessionResult(result) }
    fun getResultsForSession(sessionId: Int) = repository.getResultsForSession(sessionId)

    fun addSubscription(subscription: Subscription) {
        viewModelScope.launch {
            repository.insertSubscription(SubscriptionEntity.fromDomainModel(subscription))
        }
    }

    fun updateSubscription(subscription: Subscription) {
        viewModelScope.launch {
            repository.updateSubscription(SubscriptionEntity.fromDomainModel(subscription))
        }
    }

    fun deleteSubscription(subscription: Subscription) {
        viewModelScope.launch {
            repository.deleteSubscription(SubscriptionEntity.fromDomainModel(subscription))
        }
    }

    fun addClient(client: ClientEntity) {
        viewModelScope.launch {
            repository.insertClient(client)
        }
    }

    fun updateClient(client: ClientEntity) {
        viewModelScope.launch {
            repository.updateClient(client)
        }
    }

    fun deleteClient(client: ClientEntity) {
        viewModelScope.launch {
            repository.deleteClient(client)
        }
    }
}
