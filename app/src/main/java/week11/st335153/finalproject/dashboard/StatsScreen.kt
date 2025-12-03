package week11.st335153.finalproject.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun StatsScreen(
    navController: NavController,
    viewModel: DashboardViewModel = viewModel()
) {
    val workouts by viewModel.workouts.collectAsState()

    val totalSteps = viewModel.totalSteps
    val totalMinutes = viewModel.totalMinutes
    val count = viewModel.workoutCount
    val avgSteps = if (count > 0) totalSteps / count else 0
    val avgMinutes = if (count > 0) totalMinutes / count else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Statistics", style = MaterialTheme.typography.headlineMedium)

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Overall", fontWeight = FontWeight.SemiBold)
                Text("Total workouts: $count")
                Text("Total steps: $totalSteps")
                Text("Total minutes: $totalMinutes")
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Average per workout", fontWeight = FontWeight.SemiBold)
                Text("Avg steps: $avgSteps")
                Text("Avg minutes: $avgMinutes")
            }
        }

        if (workouts.isNotEmpty()) {
            val last = workouts.first()
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Last workout", fontWeight = FontWeight.SemiBold)
                    Text("${last.steps} steps • ${last.minutes} mins")
                    Text(last.notes.ifBlank { "No notes" })
                    Text(last.dateFormatted, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Back")
        }
    }
}
