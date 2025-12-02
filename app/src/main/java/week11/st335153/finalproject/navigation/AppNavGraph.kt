package week11.st335153.finalproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable

import week11.st335153.finalproject.auth.LoginScreen
import week11.st335153.finalproject.auth.RegisterScreen
import week11.st335153.finalproject.dashboard.DashboardScreen
import week11.st335153.finalproject.history.HistoryScreen

@Composable
fun AppNavGraph() {
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = "login"
    ) {
        composable("login") { LoginScreen(nav) }
        composable("register") { RegisterScreen(nav) }
        composable("dashboard") { DashboardScreen(nav) }
        composable("history") { HistoryScreen(nav) }
    }
}
