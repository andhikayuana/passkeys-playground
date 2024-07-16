package id.yuana.passkeys.playground.ui.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import id.yuana.passkeys.playground.navigation.navigation.OnNavigate

@Composable
fun HomeScreen(
    onNavigate: OnNavigate,
) {
    Scaffold {
        Column(modifier = Modifier.padding(it)) {
            Text(text = "Home")
        }
    }
}