package com.example.personalgymapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalgymapp.database.entity.*
import com.example.personalgymapp.model.*
import com.example.personalgymapp.notifications.NotificationHelper
import com.example.personalgymapp.repository.ClientRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ClientViewModel(application: Application, private val repository: ClientRepository) : AndroidViewModel(application) {

    private val notificationHelper = NotificationHelper(application)

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

    val subscriptionPlans: StateFlow<List<SubscriptionPlanEntity>> = repository.allSubscriptionPlans
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
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            // Seed Clients
            val currentClients = repository.allClients.first()
            if (currentClients.isEmpty()) {
                val seedClients = listOf(
                    ClientEntity(
                        name = "Γιώργος Παπαδόπουλος",
                        goal = "Απώλεια βάρους",
                        birthDate = sdf.parse("1985-06-15") ?: Date(),
                        phone = "6912345678",
                        email = "giorgos@example.com",
                        sessionsCompleted = 0,
                        nextSession = "Not scheduled"
                    ),
                    ClientEntity(
                        name = "Μαρία Κωνσταντίνου",
                        goal = "Ενδυνάμωση",
                        birthDate = sdf.parse("1992-03-20") ?: Date(),
                        phone = "6987654321",
                        email = "maria@example.com",
                        sessionsCompleted = 0,
                        nextSession = "Not scheduled"
                    )
                )
                seedClients.forEach { repository.insertClient(it) }
            }

            // Seed Exercises
            val currentExercises = repository.allExercises.first()
            if (currentExercises.isEmpty()) {
                val seedExercises = listOf(
                    Exercise(name = "Push Ups", muscleGroup = "Chest", equipment = "Bodyweight", difficulty = "Beginner", imageResName = "push_ups", instructions = "Keep your back straight and lower your chest to the floor."),
                    Exercise(name = "Bench Press", muscleGroup = "Chest", equipment = "Barbell", difficulty = "Intermediate", imageResName = "bench_press", instructions = "Lower the barbell to your mid-chest and press it back up."),
                    Exercise(name = "Squats", muscleGroup = "Legs", equipment = "Bodyweight", difficulty = "Beginner", imageResName = "squats", instructions = "Lower your hips as if sitting in a chair, keeping your knees behind your toes."),
                    Exercise(name = "Lunges", muscleGroup = "Legs", equipment = "Bodyweight", difficulty = "Beginner", imageResName = "lunges", instructions = "Step forward and lower your hips until both knees are bent at a 90-degree angle."),
                    Exercise(name = "Pull Ups", muscleGroup = "Back", equipment = "Pull-up Bar", difficulty = "Advanced", imageResName = "pull_ups", instructions = "Pull your body up until your chin is over the bar."),
                    Exercise(name = "Bent Over Row", muscleGroup = "Back", equipment = "Dumbbells", difficulty = "Intermediate", imageResName = "bent_over_row", instructions = "Hinge at the waist and pull the weights towards your lower ribs."),
                    Exercise(name = "Overhead Press", muscleGroup = "Shoulders", equipment = "Dumbbells", difficulty = "Intermediate", imageResName = "overhead_press", instructions = "Press the weights directly overhead until your arms are fully extended."),
                    Exercise(name = "Lateral Raises", muscleGroup = "Shoulders", equipment = "Dumbbells", difficulty = "Beginner", imageResName = "lateral_raises", instructions = "Raise your arms out to the sides until they are level with your shoulders."),
                    Exercise(name = "Bicep Curls", muscleGroup = "Arms", equipment = "Dumbbells", difficulty = "Beginner", imageResName = "bicep_curls", instructions = "Curl the weights toward your shoulders, keeping your elbows close to your torso."),
                    Exercise(name = "Tricep Dips", muscleGroup = "Arms", equipment = "Bench", difficulty = "Beginner", imageResName = "tricep_dips", instructions = "Lower your body by bending your elbows until they are at a 90-degree angle."),
                    Exercise(name = "Plank", muscleGroup = "Core", equipment = "Bodyweight", difficulty = "Beginner", imageResName = "plank", instructions = "Maintain a straight line from head to heels while resting on your forearms."),
                    Exercise(name = "Russian Twists", muscleGroup = "Core", equipment = "Bodyweight", difficulty = "Beginner", imageResName = "russian_twists", instructions = "Sit with knees bent and twist your torso from side to side."),
                    Exercise(name = "Running", muscleGroup = "Cardio", equipment = "Treadmill", difficulty = "Beginner", imageResName = "running", instructions = "Maintain a steady pace and focus on your breathing."),
                    Exercise(name = "Jumping Rope", muscleGroup = "Cardio", equipment = "Jump Rope", difficulty = "Beginner", imageResName = "jumping_rope", instructions = "Jump continuously while swinging the rope under your feet.")
                )
                seedExercises.forEach { repository.insertExercise(it) }
            }
        }
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
    fun addSession(session: TrainingSession) {
        viewModelScope.launch {
            val id = repository.insertSession(session).toInt()
            if (session.status == "Scheduled") {
                notificationHelper.scheduleSessionReminder(session.copy(id = id))
            }
        }
    }

    fun updateSession(session: TrainingSession) {
        viewModelScope.launch {
            repository.updateSession(session)
            if (session.status == "Scheduled") {
                notificationHelper.scheduleSessionReminder(session)
            } else {
                notificationHelper.cancelSessionReminder(session.id)
            }
        }
    }

    fun deleteSession(session: TrainingSession) {
        viewModelScope.launch {
            repository.deleteSession(session)
            notificationHelper.cancelSessionReminder(session.id)
        }
    }

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

    // Subscription Plan Methods
    fun addSubscriptionPlan(plan: SubscriptionPlanEntity) = viewModelScope.launch { repository.insertSubscriptionPlan(plan) }
    fun updateSubscriptionPlan(plan: SubscriptionPlanEntity) = viewModelScope.launch { repository.updateSubscriptionPlan(plan) }
    fun deleteSubscriptionPlan(plan: SubscriptionPlanEntity) = viewModelScope.launch { repository.deleteSubscriptionPlan(plan) }
}
