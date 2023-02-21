package com.example.activmitsu_can.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.activmitsu_can.R
import com.example.activmitsu_can.utils.collectAsEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    state: MainState,
    onTryToConnect: () -> Unit,
    onTryToDisconnect: () -> Unit,
    onDeviceIdChange: (String) -> Unit,
    errorFlow: Flow<String>
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    errorFlow.collectAsEffect { error ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                message = error
            )
        }
    }
    Box(contentAlignment = Alignment.Center) {
        Column {
            Text(
                text = "Speed: ${state.speed}\nCvt temp: ${state.cvtTemp}"
            )
            if (!state.isConnected) {
                TextField(
                    value = state.deviceId,
                    onValueChange = onDeviceIdChange,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
                )
            }
            Button(
                onClick = onTryToConnect,
                enabled = !state.isConnected
            ) {
                Text(text = stringResource(id = R.string.try_to_connect))
            }
            Button(
                onClick = onTryToDisconnect,
                enabled = state.isConnected
            ) {
                Text(text = stringResource(id = R.string.try_to_disconnect))
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .imePadding()
        )
    }
}