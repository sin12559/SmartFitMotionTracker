package week11.st335153.finalproject.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import week11.st335153.finalproject.data.Workout
import week11.st335153.finalproject.sensors.SensorsManager
import androidx.compose.ui.platform.LocalContext

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = viewModel()
) {
    val workouts by viewModel.workouts.collectAsState()
    val errorMessage by viewModel.error.collectAsState()

    val context = LocalContext.current
    val sensorsManager = remember { SensorsManager(context) }
    val steps by sensorsManager.steps.collectAsState()
    val light by sensorsManager.light.collectAsState()
    val movement by sensorsManager.movement.collectAsState()

    DisposableEffect(Unit) {
        sensorsManager.start()
        onDispose { sensorsManager.stop() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),   // <-- FIX
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Dashboard", style = MaterialTheme.typography.headlineMedium)

        SensorCards(steps, light, movement)

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { navController.navigate("addWorkout") },
                modifier = Modifier.weight(1f)
            ) { Text("Add Workout") }

            Button(
                onClick = { navController.navigate("history") },
                modifier = Modifier.weight(1f)
            ) { Text("Workout History") }
        }

        Button(
            onClick = { navController.navigate("stats") },
            modifier = Modifier.fillMaxWidth()
        ) { Text("View Stats") }

        Text(
            "Your Workouts:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        if (workouts.isEmpty()) {
            Text("No workouts yet.")
        } else {
            WorkoutList(
                workouts = workouts.take(5),
                onEdit = { w -> navController.navigate("editWorkout/${w.id}") },
                onDelete = { w -> viewModel.deleteWorkout(w.id) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---- LOGOUT BUTTON ----
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login") {
                    popUpTo("dashboard") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Logout", color = MaterialTheme.colorScheme.onError)
        }
    }
}

@Composable
private fun SensorCards(steps: Int, light: Float, movement: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                Text("Live Steps (sensor)", fontWeight = FontWeight.SemiBold)
                Text("$steps steps")
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                Text("Light Level", fontWeight = FontWeight.SemiBold)
                Text("${"%.2f".format(light)} lx")
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                Text("Movement", fontWeight = FontWeight.SemiBold)
                Text("${"%.2f".format(movement)}")
            }
        }
    }
}

@Composable
private fun WorkoutList(
    workouts: List<Workout>,
    onEdit: (Workout) -> Unit,
    onDelete: (Workout) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.heightIn(max = 220.dp)
    ) {
        items(workouts, key = { it.id }) { workout ->
            Card {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("${workout.steps} steps • ${workout.minutes} mins", fontWeight = FontWeight.SemiBold)
                        Text(workout.notes.ifBlank { "No notes" })
                        Text(workout.dateFormatted, style = MaterialTheme.typography.bodySmall)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        TextButton(onClick = { onEdit(workout) }) { Text("Edit") }
                        TextButton(onClick = { onDelete(workout) }) { Text("Delete") }
                    }
                }
            }
        }
    }
}
