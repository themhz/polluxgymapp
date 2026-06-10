package com.example.personalgymapp.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalgymapp.screens.*
import com.example.personalgymapp.viewmodel.ClientViewModel
import com.example.personalgymapp.viewmodel.SettingsViewModel

@Composable
fun AppNavigation(clientViewModel: ClientViewModel) {
    val navController = rememberNavController()
    val settingsViewModel: SettingsViewModel = viewModel()
    
    val clients by clientViewModel.clients.collectAsState()
    val exercises by clientViewModel.exercises.collectAsState()
    val workoutPlans by clientViewModel.workoutPlans.collectAsState()
    val trainingSessions by clientViewModel.trainingSessions.collectAsState()
    val subscriptions by clientViewModel.subscriptions.collectAsState()
    val subscriptionPlans by clientViewModel.subscriptionPlans.collectAsState()

    // Filter states for Exercise Library
    var exerciseSearchQuery by remember { mutableStateOf("") }
    var exerciseSelectedMuscleGroups by remember { mutableStateOf(setOf<String>()) }

    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        composable("home") {
            HomeScreen(
                onClientsClick = { navController.navigate("clients") },
                onWorkoutsClick = { navController.navigate("workoutPlans") },
                onProgressClick = { navController.navigate("progress") },
                onCalendarClick = { navController.navigate("calendar") },
                onSensorsClick = { navController.navigate("sensors") },
                onGarminClick = { navController.navigate("garminSettings") },
                onSubscriptionsClick = { navController.navigate("subscriptionPlans") },
                onSettingsClick = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                viewModel = settingsViewModel,
                clientViewModel = clientViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "subscriptionPlans?selectionMode={selectionMode}",
            arguments = listOf(navArgument("selectionMode") { 
                type = NavType.BoolType
                defaultValue = false
            })
        ) { backStackEntry ->
            val isSelectionMode = backStackEntry.arguments?.getBoolean("selectionMode") ?: false
            SubscriptionPlansScreen(
                plans = subscriptionPlans,
                isSelectionMode = isSelectionMode,
                onAddPlanClick = { navController.navigate("addSubscriptionPlan") },
                onPlanClick = { planId -> navController.navigate("editSubscriptionPlan/$planId") },
                onPlanSelected = { planId ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("selectedPlanId", planId)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("addSubscriptionPlan") {
            AddSubscriptionPlanScreen(
                onSavePlan = { plan ->
                    clientViewModel.addSubscriptionPlan(plan)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "editSubscriptionPlan/{planId}",
            arguments = listOf(navArgument("planId") { type = NavType.IntType })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getInt("planId") ?: -1
            val plan = subscriptionPlans.find { it.id == planId }
            EditSubscriptionPlanScreen(
                plan = plan,
                onSavePlan = { updatedPlan ->
                    clientViewModel.updateSubscriptionPlan(updatedPlan)
                    navController.popBackStack()
                },
                onDeletePlan = { planToDelete ->
                    clientViewModel.deleteSubscriptionPlan(planToDelete)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("subscriptions") {
            SubscriptionsScreen(
                subscriptions = subscriptions,
                onAddSubscriptionClick = { navController.navigate("addSubscription") },
                onSubscriptionClick = { subId -> navController.navigate("editSubscription/$subId") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "editSubscription/{subscriptionId}",
            arguments = listOf(navArgument("subscriptionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val subId = backStackEntry.arguments?.getInt("subscriptionId") ?: -1
            val subscription = subscriptions.find { it.id == subId }
            EditSubscriptionScreen(
                subscription = subscription,
                onSaveSubscription = { updatedSub ->
                    clientViewModel.updateSubscription(updatedSub)
                    navController.popBackStack()
                },
                onDeleteSubscription = { subToDelete ->
                    clientViewModel.deleteSubscription(subToDelete)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "addSubscription?clientId={clientId}",
            arguments = listOf(navArgument("clientId") { 
                type = NavType.IntType
                defaultValue = -1
            })
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getInt("clientId") ?: -1
            val selectedPlanId by backStackEntry.savedStateHandle.getStateFlow<Int?>("selectedPlanId", null).collectAsState()
            
            AddSubscriptionScreen(
                clients = clients,
                subscriptionPlans = subscriptionPlans,
                initialClientId = clientId,
                selectedPlanIdFromNav = selectedPlanId,
                onNavigateToSelectPlan = {
                    navController.navigate("subscriptionPlans?selectionMode=true")
                },
                onNavigateToAddPlan = {
                    navController.navigate("addSubscriptionPlan")
                },
                onSaveSubscription = { newSub ->
                    clientViewModel.addSubscription(newSub)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("sensors") {
            SensorsScreen(onBackClick = { navController.popBackStack() })
        }
        composable("garminSettings") {
            GarminSettingsScreen(onBackClick = { navController.popBackStack() })
        }
        composable("clients") {
            ClientsScreen(
                clients = clients,
                subscriptions = subscriptions,
                subscriptionPlans = subscriptionPlans,
                onBackClick = { navController.popBackStack() },
                onClientClick = { clientId -> 
                    navController.navigate("clientDetails/$clientId") 
                },
                onAddClientClick = { navController.navigate("addClient") }
            )
        }
        composable("addClient") {
            AddClientScreen(
                subscriptionPlans = subscriptionPlans,
                onSaveClient = { newClient ->
                    clientViewModel.addClient(newClient)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "clientDetails/{clientId}",
            arguments = listOf(navArgument("clientId") { type = NavType.IntType })
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getInt("clientId") ?: -1
            val client = clients.find { it.id == clientId }
            val payments by clientViewModel.getPaymentsForClient(clientId).collectAsState(initial = emptyList())
            
            ClientDetailsScreen(
                client = client,
                subscriptions = subscriptions,
                trainingSessions = trainingSessions,
                payments = payments,
                onAddPaymentClick = { id -> navController.navigate("addPayment/$id") },
                onDeletePaymentClick = { payment -> clientViewModel.deletePayment(payment) },
                onAddSubscriptionClick = { id -> navController.navigate("addSubscription?clientId=$id") },
                onEditClick = { id -> navController.navigate("editClient/$id") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "addPayment/{clientId}",
            arguments = listOf(navArgument("clientId") { type = NavType.IntType })
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getInt("clientId") ?: -1
            val client = clients.find { it.id == clientId }
            AddPaymentScreen(
                clientId = clientId,
                clientName = client?.name ?: "Unknown",
                onSavePayment = { payment ->
                    clientViewModel.addPayment(payment)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "editClient/{clientId}",
            arguments = listOf(navArgument("clientId") { type = NavType.IntType })
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getInt("clientId") ?: -1
            val client = clients.find { it.id == clientId }
            EditClientScreen(
                client = client,
                subscriptionPlans = subscriptionPlans,
                onSaveClient = { updatedClient ->
                    clientViewModel.updateClient(updatedClient)
                    navController.popBackStack()
                },
                onDeleteClient = { clientToDelete ->
                    clientViewModel.deleteClient(clientToDelete)
                    navController.navigate("clients") {
                        popUpTo("clients") { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("workoutPlans") {
            WorkoutPlansScreen(
                workoutPlans = workoutPlans,
                onWorkoutPlanClick = { planId ->
                    navController.navigate("workoutPlanDetails/$planId")
                },
                onCreateWorkoutClick = { navController.navigate("createWorkoutPlan") },
                onExerciseLibraryClick = { navController.navigate("workouts") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "workoutPlanDetails/{workoutPlanId}",
            arguments = listOf(navArgument("workoutPlanId") { type = NavType.IntType })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getInt("workoutPlanId") ?: -1
            val plan = workoutPlans.find { it.id == planId }
            WorkoutPlanDetailsScreen(
                workoutPlan = plan,
                onExerciseClick = { exerciseId ->
                    navController.navigate("editWorkoutExercise/${planId}/$exerciseId")
                },
                onEditClick = { id -> navController.navigate("editWorkoutPlan/$id") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "editWorkoutPlan/{workoutPlanId}",
            arguments = listOf(navArgument("workoutPlanId") { type = NavType.IntType })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getInt("workoutPlanId") ?: -1
            val plan = workoutPlans.find { it.id == planId }
            EditWorkoutPlanScreen(
                workoutPlan = plan,
                availableExercises = exercises,
                onSaveWorkoutPlan = { updatedPlan ->
                    clientViewModel.updateWorkoutPlan(updatedPlan)
                    navController.popBackStack()
                },
                onDeleteWorkoutPlan = { planToDelete ->
                    clientViewModel.deleteWorkoutPlan(planToDelete)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "editWorkoutExercise/{workoutPlanId}/{exerciseId}",
            arguments = listOf(
                navArgument("workoutPlanId") { type = NavType.IntType },
                navArgument("exerciseId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getInt("workoutPlanId") ?: -1
            val exerciseId = backStackEntry.arguments?.getInt("exerciseId") ?: -1
            val plan = workoutPlans.find { it.id == planId }
            
            EditWorkoutExerciseScreen(
                workoutPlan = plan,
                exerciseId = exerciseId,
                onSave = { updatedExercise ->
                    if (plan != null) {
                        val updatedExercises = plan.exercises.toMutableList()
                        val index = updatedExercises.indexOfFirst { it.exerciseId == exerciseId }
                        if (index != -1) {
                            updatedExercises[index] = updatedExercise
                            clientViewModel.updateWorkoutPlan(plan.copy(exercises = updatedExercises))
                        }
                    }
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("createWorkoutPlan") {
            CreateWorkoutPlanScreen(
                exercises = exercises,
                onSaveWorkoutPlan = { newPlan ->
                    clientViewModel.addWorkoutPlan(newPlan)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("workouts") {
            WorkoutsScreen(
                exercises = exercises,
                searchQuery = exerciseSearchQuery,
                onSearchQueryChange = { exerciseSearchQuery = it },
                selectedMuscleGroups = exerciseSelectedMuscleGroups,
                onMuscleGroupsChange = { exerciseSelectedMuscleGroups = it },
                onExerciseClick = { exerciseId ->
                    navController.navigate("exerciseDetails/$exerciseId")
                },
                onAddExerciseClick = { navController.navigate("addExercise") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("addExercise") {
            AddExerciseScreen(
                onSaveExercise = { newExercise ->
                    clientViewModel.addExercise(newExercise)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "exerciseDetails/{exerciseId}",
            arguments = listOf(navArgument("exerciseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getInt("exerciseId") ?: -1
            val exercise = exercises.find { it.id == exerciseId }
            ExerciseDetailsScreen(
                exercise = exercise,
                onEditClick = { id -> navController.navigate("editExercise/$id") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "editExercise/{exerciseId}",
            arguments = listOf(navArgument("exerciseId") { type = NavType.IntType })
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getInt("exerciseId") ?: -1
            val exercise = exercises.find { it.id == exerciseId }
            EditExerciseScreen(
                exercise = exercise,
                onSaveExercise = { updatedExercise ->
                    clientViewModel.updateExercise(updatedExercise)
                    navController.popBackStack()
                },
                onDeleteExercise = { exToDelete ->
                    clientViewModel.deleteExercise(exToDelete)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("progress") {
            // Need to update ProgressScreen to use persistent data if needed
            ProgressScreen(
                progressEntries = emptyList(), // Placeholder for now
                onProgressEntryClick = { },
                onAddProgressEntryClick = { },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("calendar") {
            CalendarScreen(
                trainingSessions = trainingSessions,
                workoutPlans = workoutPlans,
                onTrainingSessionClick = { sessionId ->
                    navController.navigate("trainingSessionDetails/$sessionId")
                },
                onAddTrainingSessionClick = { navController.navigate("addTrainingSession") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "trainingSessionDetails/{trainingSessionId}",
            arguments = listOf(navArgument("trainingSessionId") { type = NavType.IntType }),
            deepLinks = listOf(
                navDeepLink { uriPattern = "pgymapp://session/{trainingSessionId}" }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getInt("trainingSessionId") ?: -1
            val session = trainingSessions.find { it.id == sessionId }
            val plan = workoutPlans.find { it.id == session?.workoutPlanId }
            TrainingSessionDetailsScreen(
                trainingSession = session,
                workoutPlan = plan,
                availableWorkoutPlans = workoutPlans,
                onWorkoutPlanClick = { planId ->
                    navController.navigate("workoutPlanDetails/$planId")
                },
                onViewSessionResultsClick = { id ->
                    navController.navigate("sessionResults/$id")
                },
                onStartWorkoutClick = { id ->
                    navController.navigate("activeWorkout/$id")
                },
                onUpdateSession = { updatedSession ->
                    clientViewModel.updateSession(updatedSession)
                },
                onDeleteSession = { sessionToDelete ->
                    clientViewModel.deleteSession(sessionToDelete)
                    navController.popBackStack()
                },
                onEditClick = { id ->
                    navController.navigate("editTrainingSession/$id")
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "editTrainingSession/{trainingSessionId}",
            arguments = listOf(navArgument("trainingSessionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getInt("trainingSessionId") ?: -1
            val session = trainingSessions.find { it.id == sessionId }
            EditTrainingSessionScreen(
                session = session,
                workoutPlans = workoutPlans,
                onSaveTrainingSession = { updatedSession ->
                    clientViewModel.updateSession(updatedSession)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "activeWorkout/{trainingSessionId}",
            arguments = listOf(navArgument("trainingSessionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getInt("trainingSessionId") ?: -1
            val session = trainingSessions.find { it.id == sessionId }
            val plan = workoutPlans.find { it.id == session?.workoutPlanId }
            ActiveWorkoutSessionScreen(
                trainingSession = session,
                workoutPlan = plan,
                onFinishWorkout = { results ->
                    if (session != null) {
                        clientViewModel.updateSession(session.copy(status = "Completed"))
                        clients.find { it.id == session.clientId }?.let { client ->
                            clientViewModel.updateClient(client.copy(
                                sessionsCompleted = client.sessionsCompleted + 1
                            ))
                        }
                    }
                    results.forEach { clientViewModel.addSessionResult(it) }
                    navController.navigate("sessionResults/$sessionId") {
                        popUpTo("trainingSessionDetails/$sessionId")
                    }
                },
                onCancelWorkout = { navController.popBackStack() }
            )
        }
        composable("addTrainingSession") {
            AddTrainingSessionScreen(
                clients = clients,
                workoutPlans = workoutPlans,
                onSaveTrainingSession = { newSession ->
                    clientViewModel.addSession(newSession)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "sessionResults/{trainingSessionId}",
            arguments = listOf(navArgument("trainingSessionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getInt("trainingSessionId") ?: -1
            val session = trainingSessions.find { it.id == sessionId }
            val plan = workoutPlans.find { it.id == session?.workoutPlanId }
            val results by clientViewModel.getResultsForSession(sessionId).collectAsState(initial = emptyList())
            SessionResultsScreen(
                trainingSession = session,
                workoutPlan = plan,
                sessionExerciseResults = results,
                onAddExerciseResultClick = { id ->
                    navController.navigate("addSessionExerciseResult/$id")
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "addSessionExerciseResult/{trainingSessionId}",
            arguments = listOf(navArgument("trainingSessionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getInt("trainingSessionId") ?: -1
            val session = trainingSessions.find { it.id == sessionId }
            AddSessionExerciseResultScreen(
                trainingSession = session,
                exercises = exercises,
                onSaveExerciseResult = { newResult ->
                    clientViewModel.addSessionResult(newResult)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
