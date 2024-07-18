package id.yuana.passkeys.playground.ui.feature.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import id.yuana.passkeys.playground.R
import id.yuana.passkeys.playground.navigation.navigation.OnNavigate
import id.yuana.passkeys.playground.navigation.navigation.UiEvent
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    onNavigate: OnNavigate,
    viewModel: SplashViewModel = koinViewModel()
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.onEvent(SplashEvent.OnLoad)
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event)
                else -> Unit
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(id = R.string.app_name))
    }
}