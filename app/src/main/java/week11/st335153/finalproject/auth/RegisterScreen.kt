package week11.st335153.finalproject.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun RegisterScreen(nav: NavController) {
    val vm = remember { AuthViewModel() }
    val state = vm.state.collectAsState()

    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    Column(Modifier.padding(24.dp)) {

        Text("Register", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(Modifier.height(8.dp))

        TextField(value = pass, onValueChange = { pass = it }, label = { Text("Password") })

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            vm.register(email, pass) { nav.navigate("dashboard") }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Create Account")
        }

        TextButton(onClick = { nav.popBackStack() }) {
            Text("Back")
        }

        state.value.error?.let {
            Text("Error: $it", color = MaterialTheme.colorScheme.error)
        }
    }
}
