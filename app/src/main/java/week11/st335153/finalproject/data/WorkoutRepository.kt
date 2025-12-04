package week11.st335153.finalproject.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class WorkoutRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private fun workoutsCollection() =
        auth.currentUser?.uid?.let { uid ->
            firestore.collection("users")
                .document(uid)
                .collection("workouts")
        }

    /**
     * Realtime stream of workouts ordered by date (newest first).
     */
    fun getWorkoutsFlow(): Flow<List<Workout>> = callbackFlow {
        val collection = workoutsCollection()
        if (collection == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val registration = collection
            .orderBy("date")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // In a real app you'd log this; here we just keep the last good value.
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { doc ->
                    val workout = doc.toObject<Workout>()
                    workout?.copy(id = doc.id)
                }.orEmpty()

                // newest first
                trySend(list.sortedByDescending { it.date })
            }

        awaitClose { registration.remove() }
    }

    suspend fun addWorkout(steps: Int, minutes: Int, notes: String) {
        val collection = workoutsCollection() ?: return
        val docRef = collection.document()
        val workout = Workout(
            id = docRef.id,
            steps = steps,
            minutes = minutes,
            notes = notes,
            date = System.currentTimeMillis()
        )
        docRef.set(workout).await()
    }

    suspend fun updateWorkout(id: String, steps: Int, minutes: Int, notes: String) {
        val collection = workoutsCollection() ?: return
        collection.document(id).update(
            mapOf(
                "steps" to steps,
                "minutes" to minutes,
                "notes" to notes
            )
        ).await()
    }

    suspend fun deleteWorkout(id: String) {
        val collection = workoutsCollection() ?: return
        collection.document(id).delete().await()
    }
}
