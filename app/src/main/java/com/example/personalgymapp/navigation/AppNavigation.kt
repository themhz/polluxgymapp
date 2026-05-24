package com.example.personalgymapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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

    NavHost(
        navController = navController,
        startDestination = "login",
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
                onSensorsClick = { navController.navigate("sensors") }
            )
        }
        composable("sensors") {
            SensorsScreen(onBackClick = { navController.popBackStack() })
        }
        composable("clients") {
            ClientsScreen(
                clients = clients,
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
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("createWorkoutPlan") {
            CreateWorkoutPlanScreen(
                clients = clients,
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
