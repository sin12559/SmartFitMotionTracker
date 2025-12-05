package week11.st335153.finalproject.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val auth = FirebaseAuth.getInstance()

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Register", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(value = email, onValueChange = { email = it },
            label = { Text("Email") }, modifier = Modifier.fillMaxWidth(),enabled = !loading)


        Spacer(Modifier.height(12.dp))

        OutlinedTextField(value = password, onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),enabled = !loading)

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(value = confirm, onValueChange = { confirm = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),enabled = !loading)

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                error = null

                when {
                    email.isBlank() || password.isBlank() || confirm.isBlank() ->
                        error = "All fields are required."
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                        error = "Please enter a valid email."
                    password.length < 6 ->
                        error = "Password must be at least 6 characters."
                    password != confirm ->
                        error = "Passwords do not match."
                    else -> {
                        loading = true
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                loading = false
                                if (task.isSuccessful) {
                                    onRegisterSuccess()
                                } else {
                                    error = when (task.exception) {
                                        is FirebaseAuthUserCollisionException -> "This email is already registered."
                                        is FirebaseAuthWeakPasswordException -> "Password is too weak."
                                        is FirebaseAuthInvalidCredentialsException -> "Invalid email format."
                                        else -> "Registration failed. Please try again."
                                    }
                                }
                            }
                    }
                }
            }, modifier = Modifier.fillMaxWidth()) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Register")
            }
        }
        error?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("Already have an account? Login")
        }
    }
}
