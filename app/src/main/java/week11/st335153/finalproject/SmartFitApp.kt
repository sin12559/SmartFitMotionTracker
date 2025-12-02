package week11.st335153.finalproject

import androidx.compose.runtime.Composable
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import week11.st335153.finalproject.navigation.AppNavGraph

@Composable
fun SmartFitApp() {
    Surface(color = MaterialTheme.colorScheme.background) {
        AppNavGraph()
    }
}