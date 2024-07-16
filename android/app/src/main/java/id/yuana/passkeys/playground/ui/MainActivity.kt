package id.yuana.passkeys.playground.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import id.yuana.passkeys.playground.navigation.navigation.RootNav
import id.yuana.passkeys.playground.ui.theme.PasskeysPlaygroundClientTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PasskeysPlaygroundClientTheme {
                RootNav {}
            }
        }
    }
}