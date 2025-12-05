package week11.st335153.finalproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import week11.st335153.finalproject.auth.LoginScreen
import week11.st335153.finalproject.auth.RegisterScreen
import week11.st335153.finalproject.dashboard.AddWorkoutScreen
import week11.st335153.finalproject.dashboard.DashboardScreen
import week11.st335153.finalproject.dashboard.EditWorkoutScreen
import week11.st335153.finalproject.dashboard.StatsScreen
import week11.st335153.finalproject.history.HistoryScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val isLoggedIn = auth.currentUser != null

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "dashboard" else "login"
    ) {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("dashboard") {
            DashboardScreen(navController)
        }

        // 🔽 AddWorkout now accepts initialSteps as a route argument
        composable(
            route = "addWorkout/{initialSteps}",
            arguments = listOf(
                navArgument("initialSteps") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val stepsArg = backStackEntry.arguments?.getInt("initialSteps") ?: 0
            AddWorkoutScreen(
                navController = navController,
                initialSteps = stepsArg
            )
        }

        composable("history") {
            HistoryScreen(navController)
        }

        composable("stats") {
            StatsScreen(navController)
        }

        composable("editWorkout/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            EditWorkoutScreen(
                navController = navController,
                workoutId = id
            )
        }
    }
}
