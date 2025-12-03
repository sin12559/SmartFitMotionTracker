package week11.st335153.finalproject.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import week11.st335153.finalproject.data.Workout

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: WorkoutHistoryViewModel = viewModel()
) {
    val workouts by viewModel.workouts.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Workout History", style = MaterialTheme.typography.headlineMedium)

        if (workouts.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No workouts saved yet.")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(workouts, key = { it.id }) { workout ->
                    HistoryItem(
                        workout = workout,
                        onDelete = { viewModel.deleteWorkout(workout.id) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Back")
        }
    }
}

@Composable
private fun HistoryItem(
    workout: Workout,
    onDelete: () -> Unit
) {
    Card {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "${workout.steps} steps • ${workout.minutes} mins",
                    fontWeight = FontWeight.SemiBold
                )
                Text(workout.notes.ifBlank { "No notes" })
                Text(
                    workout.dateFormatted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            TextButton(onClick = onDelete) {
                Text("Delete")
            }
        }
    }
}
