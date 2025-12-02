package week11.st335153.finalproject.data

sealed class FirestoreResult<out T> {
    data class Success<T>(val data: T) : FirestoreResult<T>()
    data class Error(val message: String) : FirestoreResult<Nothing>()
    object Loading : FirestoreResult<Nothing>()
}
