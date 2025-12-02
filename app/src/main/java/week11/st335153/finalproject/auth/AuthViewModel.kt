package week11.st335153.finalproject.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val loading: Boolean = false,
    val error: String? = null
)

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        _state.value = AuthState(loading = true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                _state.value = AuthState(error = e.message)
            }
    }

    fun register(email: String, password: String, onSuccess: () -> Unit) {
        _state.value = AuthState(loading = true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                _state.value = AuthState(error = e.message)
            }
    }

    fun logout(onSuccess: () -> Unit) {
        auth.signOut()
        onSuccess()
    }
}
