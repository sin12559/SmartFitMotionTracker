package week11.st335153.finalproject.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun AddWorkoutScreen(
    navController: NavController,
    viewModel: DashboardViewModel = viewModel()
) {
    var steps by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) } // <-- loading state



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Add Workout", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = steps,
            onValueChange = { steps = it },
            label = { Text("Steps") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        )

        OutlinedTextField(
            value = minutes,
            onValueChange = { minutes = it },
            label = { Text("Minutes") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        )
        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }


        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                error = null // Clear previous error

                val s = steps.toIntOrNull()
                val m = minutes.toIntOrNull()

                when {
                    steps.isBlank() || minutes.isBlank() ->
                        error = "Steps and Minutes cannot be empty."
                    s == null || m == null ->
                        error = "Steps and Minutes must be numbers."
                    s <= 0 || m <= 0 ->
                        error = "Steps and Minutes must be greater than 0."
                    else -> {
                        viewModel.saveWorkout(
                            steps = s,
                            minutes = m,
                            notes = notes
                        ) {
                            navController.popBackStack()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Save")
            }
        }
    }
}

