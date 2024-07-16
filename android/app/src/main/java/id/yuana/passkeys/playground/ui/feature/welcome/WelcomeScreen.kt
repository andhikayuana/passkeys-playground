package id.yuana.passkeys.playground.ui.feature.welcome

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import id.yuana.passkeys.playground.R
import id.yuana.passkeys.playground.navigation.navigation.OnNavigate
import id.yuana.passkeys.playground.navigation.navigation.UiEvent
import id.yuana.passkeys.playground.ui.component.LoadingDialog
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun WelcomeScreen(
    onNavigate: OnNavigate,
    credentialManager: CredentialManager = koinInject(),
    viewModel: WelcomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event)
                is UiEvent.ShowMessage -> when (event.type) {
                    UiEvent.ShowMessage.Type.Error -> Toast.makeText(
                        context,
                        event.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    else -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                else -> Unit
            }
        }
    }

    LaunchedEffect(key1 = state.createPublicKeyCredentialRequest) {
        state.createPublicKeyCredentialRequest?.let {
            try {
                val createPublicKeyCredentialResponse = credentialManager.createCredential(
                    context,
                    it
                ) as CreatePublicKeyCredentialResponse
                viewModel.onEvent(
                    WelcomeEvent.OnCreateCredentialResponseSuccess(
                        createPublicKeyCredentialResponse
                    )
                )

            } catch (e: Exception) {
                Log.d("YUANA", "ERROR -> ${e.message}")
                viewModel.handleError(e)
            }
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                placeholder = {
                    Text(text = stringResource(id = R.string.please_type_your_username))
                },
                value = state.username,
                onValueChange = {
                    viewModel.onEvent(WelcomeEvent.OnUsernameChange(it))
                },
                maxLines = 1
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(onClick = { viewModel.onEvent(WelcomeEvent.OnRegisterClick) }) {
                    Text(text = stringResource(id = R.string.label_register))
                }
                Button(onClick = { viewModel.onEvent(WelcomeEvent.OnLoginClick) }) {
                    Text(text = stringResource(id = R.string.label_login))
                }
            }
        }
    }

    if (state.isLoading) {
        LoadingDialog()
    }

}