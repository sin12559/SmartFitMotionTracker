package week11.st335153.finalproject.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val backgroundColor = Color(0xFFF7ECFF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "←  Back to Login",
                color = Color(0xFF2D004D),
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .clickable { onNavigateToLogin() }
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Create Account",
                color = Color(0xFF2D004D),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Join us to track your fitness journey",
                color = Color(0xFF2D004D).copy(alpha = 0.7f),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(0.88f),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !loading,
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !loading,
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = confirm,
                        onValueChange = { confirm = it },
                        label = { Text("Confirm Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !loading,
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(Modifier.height(22.dp))

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
                                                    is FirebaseAuthUserCollisionException ->
                                                        "This email is already registered."
                                                    is FirebaseAuthWeakPasswordException ->
                                                        "Password is too weak."
                                                    is FirebaseAuthInvalidCredentialsException ->
                                                        "Invalid email format."
                                                    else -> "Registration failed. Please try again."
                                                }
                                            }
                                        }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8E44AD),
                            contentColor = Color.White
                        ),
                        enabled = !loading
                    ) {
                        Text(
                            text = if (loading) "Please wait…" else "Create Account",
                            fontSize = 16.sp
                        )
                    }

                    error?.let {
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(Modifier.height(18.dp))
                }
            }
        }
    }
}
