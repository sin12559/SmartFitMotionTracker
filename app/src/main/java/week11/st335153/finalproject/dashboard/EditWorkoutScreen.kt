package week11.st335153.finalproject.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
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
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Workout",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {

                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    OutlinedTextField(
                        value = steps,
                        onValueChange = { steps = it.filter(Char::isDigit) },
                        label = { Text("Steps") },
                        leadingIcon = {
                            Icon(Icons.AutoMirrored.Filled.DirectionsRun, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !loading,
                        shape = RoundedCornerShape(20.dp)
                    )

                    OutlinedTextField(
                        value = minutes,
                        onValueChange = { minutes = it.filter(Char::isDigit) },
                        label = { Text("Minutes") },
                        leadingIcon = {
                            Icon(Icons.Default.Timer, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !loading,
                        shape = RoundedCornerShape(20.dp)
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes") },
                        leadingIcon = {
                            Icon(Icons.Default.Note, contentDescription = null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 90.dp),
                        enabled = !loading,
                        shape = RoundedCornerShape(20.dp),
                        maxLines = 4
                    )

                    error?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Button(
                    onClick = {
                        val s = steps.toIntOrNull()
                        val m = minutes.toIntOrNull()

                        when {
                            steps.isBlank() || minutes.isBlank() ->
                                error = "Steps and Minutes cannot be empty."

                            s == null || m == null ->
                                error = "Steps and Minutes must be valid numbers."

                            s <= 0 || m <= 0 ->
                                error = "Values must be greater than 0."

                            else -> {
                                loading = true
                                viewModel.updateWorkout(
                                    id = workoutId,
                                    steps = s,
                                    minutes = m,
                                    notes = notes
                                ) {
                                    loading = false
                                    navController.popBackStack()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(20.dp),
                    enabled = !loading
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Update")
                    }
                }

                OutlinedButton(
                    onClick = {
                        loading = true
                        viewModel.deleteWorkout(workoutId)
                        loading = false
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(20.dp),
                    enabled = !loading,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Delete")
                }
            }
        }
    }
}