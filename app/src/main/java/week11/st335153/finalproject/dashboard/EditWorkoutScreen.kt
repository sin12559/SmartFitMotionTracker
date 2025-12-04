package week11.st335153.finalproject.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun EditWorkoutScreen(
    navController: NavController,
    workoutId: String,
    viewModel: DashboardViewModel = viewModel()
) {
    val workout = viewModel.getWorkoutById(workoutId)

    var steps by remember(workout) { mutableStateOf(workout?.steps?.toString() ?: "") }
    var minutes by remember(workout) { mutableStateOf(workout?.minutes?.toString() ?: "") }
    var notes by remember(workout) { mutableStateOf(workout?.notes ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Edit Workout", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = steps,
            onValueChange = { steps = it },
            label = { Text("Steps") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = minutes,
            onValueChange = { minutes = it },
            label = { Text("Minutes") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                val s = steps.toIntOrNull() ?: 0
                val m = minutes.toIntOrNull() ?: 0

                viewModel.updateWorkout(
                    id = workoutId,
                    steps = s,
                    minutes = m,
                    notes = notes
                ) {
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update")
        }

        Button(
            onClick = {
                viewModel.deleteWorkout(workoutId)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Delete")
        }
    }
}
