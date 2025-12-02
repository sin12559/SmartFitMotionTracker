package week11.st335153.finalproject.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ActivityRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _historyState =
        MutableStateFlow<FirestoreResult<List<ActivityModel>>>(FirestoreResult.Loading)
    val historyState = _historyState.asStateFlow()

    fun saveActivity(steps: Float, light: Float, movement: Float) {
        val uid = auth.currentUser?.uid ?: return

        val data = ActivityModel(steps, light, movement)

        db.collection("users").document(uid)
            .collection("activities")
            .add(data)
    }

    suspend fun loadHistory() {
        val uid = auth.currentUser?.uid ?: return

        _historyState.value = FirestoreResult.Loading

        try {
            val snapshot = db.collection("users").document(uid)
                .collection("activities")
                .get()
                .await()

            val list = snapshot.toObjects(ActivityModel::class.java)

            _historyState.value = FirestoreResult.Success(list)

        } catch (e: Exception) {
            _historyState.value = FirestoreResult.Error(e.message ?: "Error")
        }
    }
}
