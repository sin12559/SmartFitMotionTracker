package week11.st335153.finalproject.auth

import androidx.compose.foundation.background
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
import com.google.firebase.auth.FirebaseAuthInvalidUserException

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val auth = FirebaseAuth.getInstance()
    val backgroundColor = Color(0xFFF7ECFF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Welcome Back",
                color = Color(0xFF2D004D),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Sign in to continue your fitness journey",
                color = Color(0xFF2D004D).copy(alpha = 0.7f),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(28.dp))
            Card(
                modifier = Modifier.fillMaxWidth(0.85f),
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

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            error = null
                            if (email.isBlank() || password.isBlank()) {
                                error = "Please enter both email and password."
                                return@Button
                            }
                            loading = true

                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    loading = false
                                    if (task.isSuccessful) {
                                        onLoginSuccess()
                                    } else {
                                        error = when (task.exception) {
                                            is FirebaseAuthInvalidUserException ->
                                                "No account found with this email."
                                            is FirebaseAuthInvalidCredentialsException ->
                                                "Incorrect email or password."
                                            else -> "Login failed. Please try again."
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
                            text = if (loading) "Please wait…" else "Login",
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

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Don't have an account? ",
                            color = Color(0xFF2D004D)
                        )
                        TextButton(
                            onClick = onNavigateToRegister,
                            enabled = !loading,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                "Register",
                                color = Color(0xFF8E44AD),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
