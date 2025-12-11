package week11.st335153.finalproject.dashboard

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import week11.st335153.finalproject.data.Workout
import week11.st335153.finalproject.sensors.SensorsManager

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = viewModel()
) {
    val context = LocalContext.current

    val activityPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            activityPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    var locationText by remember { mutableStateOf("Requesting location…") }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                fetchLastLocationText(context) { txt -> locationText = txt }
            } else {
                locationText = "Location permission denied"
            }
        }

    LaunchedEffect("locationPermission") {
        val hasLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasLocation) {
            fetchLastLocationText(context) { txt -> locationText = txt }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val workouts by viewModel.workouts.collectAsState()
    val errorMessage by viewModel.error.collectAsState()

    val sensorsManager = remember { SensorsManager(context) }
    val steps by sensorsManager.steps.collectAsState()
    val light by sensorsManager.light.collectAsState()
    val movement by sensorsManager.movement.collectAsState()

    DisposableEffect(Unit) {
        sensorsManager.start()
        onDispose { sensorsManager.stop() }
    }

    val sensorSaysDark = light < 20f
    val isDarkMode = sensorSaysDark

    val customDarkColors = darkColorScheme(
        primary = Color(0xFFB388FF),
        secondary = Color(0xFFCE93D8),
        background = Color(0xFF120022),
        surface = Color(0xFF1E1033),
        onPrimary = Color.White,
        onBackground = Color(0xFFEDE7F6),
        onSurface = Color(0xFFEDE7F6)
    )

    val customLightColors = lightColorScheme(
        primary = Color(0xFF8E44AD),
        secondary = Color(0xFFBB86FC),
        background = Color(0xFFF7ECFF),
        surface = Color.White,
        onPrimary = Color.White,
        onBackground = Color(0xFF2D004D),
        onSurface = Color(0xFF2D004D),
        surfaceVariant = Color(0xFFEAD7FF)
    )

    MaterialTheme(colorScheme = if (isDarkMode) customDarkColors else customLightColors) {

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Dashboard",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                Text(
                    text = "Track your daily activities",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Activity Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        SummaryRow(
                            icon = Icons.Default.DirectionsRun,
                            label = "Steps",
                            value = "$steps"
                        )
                        SummaryRow(
                            icon = Icons.Default.LightMode,
                            label = "Light",
                            value = "%.1f lx".format(light)
                        )
                        SummaryRow(
                            icon = Icons.Default.MonitorWeight,
                            label = "Movement",
                            value = "%.2f".format(movement)
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Current Location",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = locationText,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                errorMessage?.let { msg ->
                    if (msg.isNotBlank()) {
                        Text(
                            text = msg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Start
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "Sensor Status",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text("Theme: ${if (isDarkMode) "Dark" else "Light"} mode",
                            style = MaterialTheme.typography.bodySmall)
                        Text("Light sensor: %.2f lx".format(light),
                            style = MaterialTheme.typography.bodySmall)
                        Text("Movement value: %.2f".format(movement),
                            style = MaterialTheme.typography.bodySmall)
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    FilledTonalButton(
                        onClick = { navController.navigate("addWorkout/$steps") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(Icons.Default.Timeline, contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp))
                        Text("Save Activity Summary")
                    }

                    OutlinedButton(
                        onClick = { navController.navigate("history") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(Icons.Default.History, contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp))
                        Text("Go to History")
                    }

                    OutlinedButton(
                        onClick = { navController.navigate("stats") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(Icons.Default.Timeline, contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp))
                        Text("View Stats")
                    }
                }

                Text(
                    text = "Recent Workouts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                if (workouts.isEmpty()) {
                    Text(
                        text = "No workouts yet. Start moving to see history here!",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    WorkoutList(
                        workouts = workouts.take(5),
                        onEdit = { w -> navController.navigate("editWorkout/${w.id}") },
                        onDelete = { w -> viewModel.deleteWorkout(w.id) }
                    )
                }
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(
                        Icons.Default.Logout,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Logout", color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    }
}

@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
private fun fetchLastLocationText(
    context: Context,
    onResult: (String) -> Unit
) {
    val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    fusedClient.lastLocation
        .addOnSuccessListener { loc ->
            if (loc != null) {
                onResult("Lat: %.5f  |  Lng: %.5f".format(loc.latitude, loc.longitude))
            } else {
                onResult("No last location available")
            }
        }
        .addOnFailureListener {
            onResult("Failed to get location")
        }
}

@Composable
private fun SummaryRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, label, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
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
        modifier = Modifier.heightIn(max = 260.dp)
    ) {
        items(workouts, key = { it.id }) { workout ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(Modifier.weight(1f)) {
                        Text(
                            "${workout.steps} steps • ${workout.minutes} mins",
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(workout.notes.ifBlank { "No notes" },
                            style = MaterialTheme.typography.bodySmall)
                        Text(
                            workout.dateFormatted,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TextButton(onClick = { onEdit(workout) }) { Text("Edit") }
                        TextButton(onClick = { onDelete(workout) }) { Text("Delete") }
                    }
                }
            }
        }
    }
}