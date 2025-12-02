package week11.st335153.finalproject.dashboard

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import week11.st335153.finalproject.data.ActivityRepository
import com.google.firebase.auth.FirebaseAuth

@Composable
fun DashboardScreen(nav: NavController) {
    val context = LocalContext.current
    val sensorManager = remember { SensorsManager(context) }

    val steps = sensorManager.steps.collectAsState()
    val light = sensorManager.light.collectAsState()
    val movement = sensorManager.movement.collectAsState()

    val repo = remember { ActivityRepository() }

    DisposableEffect(Unit) {
        sensorManager.start()
        onDispose { sensorManager.stop() }
    }

    Column(Modifier.padding(20.dp)) {
        Text("Dashboard", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        Text("Steps: ${steps.value}")
        Text("Light: ${light.value}")
        Text("Movement: ${movement.value}")

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                repo.saveActivity(
                    steps.value,
                    light.value,
                    movement.value
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Activity Summary")
        }

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = { nav.navigate("history") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go to History")
        }

        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                nav.navigate("login") { popUpTo("dashboard") { inclusive = true } }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}
