package week11.st335153.finalproject.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkoutScreen(
    navController: NavController,
    viewModel: DashboardViewModel = viewModel(),
    initialSteps: Int = 0
) {
    var stepsText by remember { mutableStateOf(if (initialSteps > 0) initialSteps.toString() else "") }
    var minutesText by remember { mutableStateOf("") }
    var notesText by remember { mutableStateOf("") }
    val errorMessage by viewModel.error.collectAsState()
    val backgroundColor = Color(0xFFF7ECFF)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Workout", fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = Color(0xFF8E44AD),
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    "Log your activity",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF2D004D),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Keep track of your daily progress",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF2D004D).copy(alpha = 0.7f)
                )
            }

            if (initialSteps > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(Color.White),
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.DirectionsRun,
                                contentDescription = null,
                                tint = Color(0xFF8E44AD)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Detected Steps", fontWeight = FontWeight.Bold)
                        }
                        Text(
                            "$initialSteps steps",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D004D)
                        )
                        Text(
                            "This value has been pre-filled.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(Color.White),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = stepsText,
                        onValueChange = { stepsText = it.filter(Char::isDigit) },
                        label = { Text("Steps") },
                        leadingIcon = {
                            Icon(Icons.Default.DirectionsRun, contentDescription = null)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    OutlinedTextField(
                        value = minutesText,
                        onValueChange = { minutesText = it.filter(Char::isDigit) },
                        label = { Text("Minutes") },
                        leadingIcon = {
                            Icon(Icons.Default.Timer, contentDescription = null)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("Notes (optional)") },
                        leadingIcon = {
                            Icon(Icons.Default.EditNote, contentDescription = null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 90.dp),
                        maxLines = 4,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        val steps = stepsText.toIntOrNull() ?: 0
                        val minutes = minutesText.toIntOrNull() ?: 0
                        viewModel.saveWorkout(
                            steps = steps,
                            minutes = minutes,
                            notes = notesText
                        ) {
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8E44AD),
                        contentColor = Color.White
                    )
                ) {
                    Text("Save")
                }
            }
        }
    }
}
