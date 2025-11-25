package week11.st335153.finalproject.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import week11.st335153.finalproject.data.FirestoreResult

@Composable
fun HistoryScreen(nav: NavController) {

    val vm = remember { HistoryViewModel() }
    val state = vm.state.collectAsState()

    Column(Modifier.padding(20.dp)) {
        Text("History", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(20.dp))

        when (val s = state.value) {
            is FirestoreResult.Loading -> CircularProgressIndicator()

            is FirestoreResult.Error ->
                Text("Error: ${s.message}", color = MaterialTheme.colorScheme.error)

            is FirestoreResult.Success -> {
                LazyColumn {
                    items(s.data) { item ->
                        Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Steps: ${item.steps}")
                                Text("Light: ${item.light}")
                                Text("Movement: ${item.movement}")
                                Text("Time: ${item.timestamp}")
                            }
                        }
                    }
                }
            }
        }
    }
}
