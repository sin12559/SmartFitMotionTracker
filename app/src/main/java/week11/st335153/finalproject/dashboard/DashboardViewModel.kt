package week11.st335153.finalproject.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import week11.st335153.finalproject.data.Workout
import week11.st335153.finalproject.data.WorkoutRepository
import com.google.firebase.auth.FirebaseAuth

class DashboardViewModel : ViewModel() {

    private val repo = WorkoutRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())
    val workouts: StateFlow<List<Workout>> = _workouts.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        val user = auth.currentUser
        if (user != null) {
            observeWorkouts()
        } else {
            _workouts.value = emptyList()
        }
    }

    private fun observeWorkouts() {
        viewModelScope.launch {
            repo.getWorkoutsFlow()
                .catch { e -> _error.value = e.message }
                .collect { list -> _workouts.value = list }
        }
    }

    fun saveWorkout(steps: Int, minutes: Int, notes: String, onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                repo.addWorkout(steps, minutes, notes)
                onDone()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun updateWorkout(id: String, steps: Int, minutes: Int, notes: String, onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                repo.updateWorkout(id, steps, minutes, notes)
                onDone()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deleteWorkout(id: String) {
        viewModelScope.launch {
            try {
                repo.deleteWorkout(id)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun getWorkoutById(id: String): Workout? =
        _workouts.value.firstOrNull { it.id == id }
    val totalSteps: Int
        get() = _workouts.value.sumOf { it.steps }

    val totalMinutes: Int
        get() = _workouts.value.sumOf { it.minutes }

    val workoutCount: Int
        get() = _workouts.value.size
}