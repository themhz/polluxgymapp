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

    val allResults: StateFlow<List<SessionExerciseResult>> = repository.allResults
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

    fun deletePayment(payment: Payment) {
        viewModelScope.launch {
            repository.deletePayment(PaymentEntity.fromDomainModel(payment))

            // Update totalPaid in subscription (subtract)
            val clientSubs = repository.getSubscriptionsForClient(payment.clientId).first()
            val activeSub = clientSubs.find { it.status != "Paid" } ?: clientSubs.lastOrNull()

            if (activeSub != null) {
                val updatedSub = activeSub.copy(
                    totalPaid = (activeSub.totalPaid - payment.amount).coerceAtLeast(0.0),
                    status = if (activeSub.totalPaid - payment.amount >= activeSub.price) "Paid" else "Pending"
                )
                repository.updateSubscription(updatedSub)
            }
        }
    }

    fun seedDatabaseIfEmpty() {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            // Seed Exercises FIRST (Blocking until first result)
            if (repository.allExercises.first().isEmpty()) {
                val seedExercises = listOf(
                    Exercise(name = "Push Ups", muscleGroup = "Chest", equipment = "Bodyweight", difficulty = "Beginner", imageResName = "push_ups", instructions = "Keep your back straight."),
                    Exercise(name = "Squats", muscleGroup = "Legs", equipment = "Bodyweight", difficulty = "Beginner", imageResName = "squats", instructions = "Lower hips as if sitting."),
                    Exercise(name = "Plank", muscleGroup = "Core", equipment = "Bodyweight", difficulty = "Beginner", imageResName = "plank", instructions = "Maintain straight line."),
                    Exercise(name = "Running", muscleGroup = "Cardio", equipment = "Treadmill", difficulty = "Beginner", imageResName = "running", instructions = "Steady pace."),
                    Exercise(name = "Bicep Curls", muscleGroup = "Arms", equipment = "Dumbbells", difficulty = "Beginner", imageResName = "bicep_curls", instructions = "Curl towards shoulders.")
                )
                seedExercises.forEach { repository.insertExercise(it) }
            }

            // Seed Subscription Plans
            if (repository.allSubscriptionPlans.first().isEmpty()) {
                val subPlans = listOf(
                    SubscriptionPlanEntity(id = 1, name = "Μηνιαία Συνδρομή", price = 50.0, durationDays = 30, description = "Πρόσβαση για ένα μήνα."),
                    SubscriptionPlanEntity(id = 2, name = "Εξαμηνιαία Συνδρομή", price = 200.0, durationDays = 180, description = "Προνομιακό πακέτο 6 μηνών.")
                )
                subPlans.forEach { repository.insertSubscriptionPlan(it) }
            }

            // Seed Clients
            if (repository.allClients.first().isEmpty()) {
                val seedClients = listOf(
                    ClientEntity(id = 1, name = "Νίκος Παπαδόπουλος", goal = "Απώλεια βάρους", birthDate = sdf.parse("1990-05-10")!!, phone = "6911111111", email = "nikos@test.com", sessionsCompleted = 0, nextSession = "Not scheduled", subscriptionPlanId = 1),
                    ClientEntity(id = 2, name = "Ελένη Γεωργίου", goal = "Ενδυνάμωση", birthDate = sdf.parse("1988-11-22")!!, phone = "6922222222", email = "eleni@test.com", sessionsCompleted = 0, nextSession = "Not scheduled", subscriptionPlanId = 1),
                    ClientEntity(id = 3, name = "Κώστας Αντωνίου", goal = "Αντοχή", birthDate = sdf.parse("1995-02-14")!!, phone = "6933333333", email = "kostas@test.com", sessionsCompleted = 0, nextSession = "Not scheduled", subscriptionPlanId = 1),
                    ClientEntity(id = 4, name = "Άννα Μιχαηλίδου", goal = "Φυσική Κατάσταση", birthDate = sdf.parse("1992-08-30")!!, phone = "6944444444", email = "anna@test.com", sessionsCompleted = 0, nextSession = "Not scheduled", subscriptionPlanId = 1),
                    ClientEntity(id = 5, name = "Δημήτρης Παπάς", goal = "Bodybuilding", birthDate = sdf.parse("1985-12-05")!!, phone = "6955555555", email = "dimitris@test.com", sessionsCompleted = 0, nextSession = "Not scheduled", subscriptionPlanId = 2)
                )
                seedClients.forEach { repository.insertClient(it) }
            }

            // Seed actual Subscriptions
            if (repository.allSubscriptions.first().isEmpty()) {
                val dueDate = Calendar.getInstance().apply { add(Calendar.MONTH, 1) }
                val dueDate6 = Calendar.getInstance().apply { add(Calendar.MONTH, 6) }

                val initialSubscriptions = listOf(
                    SubscriptionEntity(clientId = 1, clientName = "Νίκος Παπαδόπουλος", planName = "Μηνιαία Συνδρομή", price = 50.0, totalPaid = 0.0, dueDate = sdf.format(dueDate.time), status = "Pending"),
                    SubscriptionEntity(clientId = 2, clientName = "Ελένη Γεωργίου", planName = "Μηνιαία Συνδρομή", price = 50.0, totalPaid = 0.0, dueDate = sdf.format(dueDate.time), status = "Pending"),
                    SubscriptionEntity(clientId = 3, clientName = "Κώστας Αντωνίου", planName = "Μηνιαία Συνδρομή", price = 50.0, totalPaid = 0.0, dueDate = sdf.format(dueDate.time), status = "Pending"),
                    SubscriptionEntity(clientId = 4, clientName = "Άννα Μιχαηλίδου", planName = "Μηνιαία Συνδρομή", price = 50.0, totalPaid = 0.0, dueDate = sdf.format(dueDate.time), status = "Pending"),
                    SubscriptionEntity(clientId = 5, clientName = "Δημήτρης Παπάς", planName = "Εξαμηνιαία Συνδρομή", price = 200.0, totalPaid = 0.0, dueDate = sdf.format(dueDate6.time), status = "Pending")
                )
                initialSubscriptions.forEach { repository.insertSubscription(it) }
            }

            // Seed Workout Plans (Wait for exercises to be present)
            val allEx = repository.allExercises.first()
            if (repository.allWorkoutPlans.first().isEmpty() && allEx.isNotEmpty()) {
                val plans = listOf(
                    WorkoutPlan(id = 1, name = "Full Body Starter", notes = "General fitness", exercises = listOf(
                        WorkoutExercise(exerciseId = allEx[0].id, exerciseName = allEx[0].name, sets = 3, reps = 10, restSeconds = 60),
                        WorkoutExercise(exerciseId = allEx[1].id, exerciseName = allEx[1].name, sets = 3, reps = 15, restSeconds = 60)
                    )),
                    WorkoutPlan(id = 2, name = "Cardio Blast", notes = "Fat burn", exercises = listOf(
                        WorkoutExercise(exerciseId = allEx[3].id, exerciseName = allEx[3].name, sets = 1, reps = 0, targetDurationSeconds = 1200, restSeconds = 0, exerciseType = "TIME", timerType = "COUNTUP")
                    )),
                    WorkoutPlan(id = 3, name = "Strength & Core", notes = "Core focus", exercises = listOf(
                        WorkoutExercise(exerciseId = allEx[2].id, exerciseName = allEx[2].name, sets = 3, reps = 0, targetDurationSeconds = 60, restSeconds = 30, exerciseType = "TIME", timerType = "COUNTDOWN"),
                        WorkoutExercise(exerciseId = allEx[0].id, exerciseName = allEx[0].name, sets = 3, reps = 12, restSeconds = 45)
                    )),
                    WorkoutPlan(id = 4, name = "Leg Day", notes = "Lower body", exercises = listOf(
                        WorkoutExercise(exerciseId = allEx[1].id, exerciseName = allEx[1].name, sets = 4, reps = 20, restSeconds = 90)
                    )),
                    WorkoutPlan(id = 5, name = "Upper Body Mix", notes = "Chest & Arms", exercises = listOf(
                        WorkoutExercise(exerciseId = allEx[0].id, exerciseName = allEx[0].name, sets = 3, reps = 15, restSeconds = 60),
                        WorkoutExercise(exerciseId = allEx[4].id, exerciseName = allEx[4].name, sets = 3, reps = 12, restSeconds = 45)
                    ))
                )
                plans.forEach { repository.insertWorkoutPlan(it) }
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

    fun resetDatabase() {
        viewModelScope.launch {
            repository.clearAllData()
            seedDatabaseIfEmpty()
        }
    }
}
