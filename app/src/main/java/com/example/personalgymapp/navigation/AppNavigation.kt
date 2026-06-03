package com.example.personalgymapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.personalgymapp.data.*
import com.example.personalgymapp.screens.*
import com.example.personalgymapp.viewmodel.ClientViewModel

@Composable
fun AppNavigation(clientViewModel: ClientViewModel) {
    val navController = rememberNavController()
    val clients by clientViewModel.clients.collectAsState()
    val exercisesList = remember { mutableStateListOf(*mockExercises.toTypedArray()) }
    val workoutPlansList = remember { mutableStateListOf(*mockWorkoutPlans.toTypedArray()) }
    val progressEntriesList = remember { mutableStateListOf(*mockProgressEntries.toTypedArray()) }
    val trainingSessionsList = remember { mutableStateListOf(*mockTrainingSessions.toTypedArray()) }
    val sessionResultsList = remember { mutableStateListOf(*mockSessionExerciseResults.toTypedArray()) }
    val subscriptions by clientViewModel.subscriptions.collectAsState()

    // Filter states for Exercise Library (hoisted to survive back navigation)
    var exerciseSearchQuery by remember { mutableStateOf("") }
    var exerciseSelectedMuscleGroups by remember { mutableStateOf(setOf<String>()) }

    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            ) {
                navController.navigate("register")
            }
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSignInClick = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                onClientsClick = { navController.navigate("clients") },
                onWorkoutsClick = { navController.navigate("workoutPlans") },
                onProgressClick = { navController.navigate("progress") },
                onCalendarClick = { navController.navigate("calendar") },
                onSensorsClick = { navController.navigate("sensors") },
                onGarminClick = { navController.navigate("garminSettings") },
                onSubscriptionsClick = { navController.navigate("subscriptions") }
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
        composable("addSubscription") {
            AddSubscriptionScreen(
                clients = clients,
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
                onBackClick = { navController.popBackStack() },
                onClientClick = { clientId -> 
                    navController.navigate("clientDetails/$clientId") 
                },
                onAddClientClick = { navController.navigate("addClient") }
            )
        }
        composable("addClient") {
            AddClientScreen(
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
            ClientDetailsScreen(
                client = client,
                subscriptions = subscriptions,
                trainingSessions = trainingSessionsList,
                onEditClick = { id -> navController.navigate("editClient/$id") },
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
                onSaveClient = { updatedClient ->
                    clientViewModel.updateClient(updatedClient)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("workoutPlans") {
            WorkoutPlansScreen(
                workoutPlans = workoutPlansList,
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
            val plan = workoutPlansList.find { it.id == planId }
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
            val plan = workoutPlansList.find { it.id == planId }
            EditWorkoutPlanScreen(
                workoutPlan = plan,
                availableExercises = exercisesList,
                onSaveWorkoutPlan = { updatedPlan ->
                    val index = workoutPlansList.indexOfFirst { it.id == planId }
                    if (index != -1) {
                        workoutPlansList[index] = updatedPlan
                    }
                    navController.popBackStack()
                },
                onDeleteWorkoutPlan = { planToDelete ->
                    workoutPlansList.removeIf { it.id == planToDelete.id }
                    navController.navigate("workoutPlans") {
                        popUpTo("workoutPlans") { inclusive = true }
                    }
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
            val plan = workoutPlansList.find { it.id == planId }
            
            EditWorkoutExerciseScreen(
                workoutPlan = plan,
                exerciseId = exerciseId,
                onSave = { updatedExercise ->
                    val planIndex = workoutPlansList.indexOfFirst { it.id == planId }
                    if (planIndex != -1) {
                        val currentPlan = workoutPlansList[planIndex]
                        val exerciseIndex = currentPlan.exercises.indexOfFirst { it.exerciseId == exerciseId }
                        if (exerciseIndex != -1) {
                            val updatedExercises = currentPlan.exercises.toMutableList()
                            updatedExercises[exerciseIndex] = updatedExercise
                            workoutPlansList[planIndex] = currentPlan.copy(exercises = updatedExercises)
                        }
                    }
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("createWorkoutPlan") {
            CreateWorkoutPlanScreen(
                exercises = exercisesList,
                onSaveWorkoutPlan = { newPlan ->
                    val nextId = (workoutPlansList.maxOfOrNull { it.id } ?: 0) + 1
                    workoutPlansList.add(newPlan.copy(id = nextId))
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("workouts") {
            WorkoutsScreen(
                exercises = exercisesList,
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
            val nextId = (exercisesList.maxOfOrNull { it.id } ?: 0) + 1
            AddExerciseScreen(
                nextId = nextId,
                onSaveExercise = { newExercise ->
                    exercisesList.add(newExercise)
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
            val exercise = exercisesList.find { it.id == exerciseId }
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
            val exercise = exercisesList.find { it.id == exerciseId }
            EditExerciseScreen(
                exercise = exercise,
                onSaveExercise = { updatedExercise ->
                    val index = exercisesList.indexOfFirst { it.id == exerciseId }
                    if (index != -1) {
                        exercisesList[index] = updatedExercise
                    }
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("progress") {
            ProgressScreen(
                progressEntries = progressEntriesList,
                onProgressEntryClick = { entryId ->
                    navController.navigate("progressDetails/$entryId")
                },
                onAddProgressEntryClick = { navController.navigate("addProgressEntry") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "progressDetails/{progressEntryId}",
            arguments = listOf(navArgument("progressEntryId") { type = NavType.IntType })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getInt("progressEntryId") ?: -1
            val entry = progressEntriesList.find { it.id == entryId }
            ProgressDetailsScreen(
                progressEntry = entry,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("addProgressEntry") {
            AddProgressEntryScreen(
                clients = clients,
                onSaveProgressEntry = { newEntry ->
                    val nextId = (progressEntriesList.maxOfOrNull { it.id } ?: 0) + 1
                    progressEntriesList.add(newEntry.copy(id = nextId))
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("calendar") {
            CalendarScreen(
                trainingSessions = trainingSessionsList,
                onTrainingSessionClick = { sessionId ->
                    navController.navigate("trainingSessionDetails/$sessionId")
                },
                onAddTrainingSessionClick = { navController.navigate("addTrainingSession") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "trainingSessionDetails/{trainingSessionId}",
            arguments = listOf(navArgument("trainingSessionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getInt("trainingSessionId") ?: -1
            val session = trainingSessionsList.find { it.id == sessionId }
            val plan = workoutPlansList.find { it.id == session?.workoutPlanId }
            TrainingSessionDetailsScreen(
                trainingSession = session,
                workoutPlan = plan,
                onWorkoutPlanClick = { planId ->
                    navController.navigate("workoutPlanDetails/$planId")
                },
                onViewSessionResultsClick = { id ->
                    navController.navigate("sessionResults/$id")
                },
                onStartWorkoutClick = { id ->
                    navController.navigate("activeWorkout/$id")
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "activeWorkout/{trainingSessionId}",
            arguments = listOf(navArgument("trainingSessionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getInt("trainingSessionId") ?: -1
            val session = trainingSessionsList.find { it.id == sessionId }
            val plan = workoutPlansList.find { it.id == session?.workoutPlanId }
            ActiveWorkoutSessionScreen(
                trainingSession = session,
                workoutPlan = plan,
                onFinishWorkout = { results ->
                    var currentMaxId = sessionResultsList.maxOfOrNull { it.id } ?: 0
                    results.forEach { result ->
                        currentMaxId++
                        sessionResultsList.add(result.copy(id = currentMaxId))
                    }
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
                workoutPlans = workoutPlansList,
                onSaveTrainingSession = { newSession ->
                    val nextId = (trainingSessionsList.maxOfOrNull { it.id } ?: 0) + 1
                    trainingSessionsList.add(newSession.copy(id = nextId))
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
            val session = trainingSessionsList.find { it.id == sessionId }
            val plan = workoutPlansList.find { it.id == session?.workoutPlanId }
            val results = sessionResultsList.filter { it.trainingSessionId == sessionId }
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
            val session = trainingSessionsList.find { it.id == sessionId }
            AddSessionExerciseResultScreen(
                trainingSession = session,
                exercises = exercisesList,
                onSaveExerciseResult = { newResult ->
                    val nextId = (sessionResultsList.maxOfOrNull { it.id } ?: 0) + 1
                    sessionResultsList.add(newResult.copy(id = nextId))
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
