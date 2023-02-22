package com.example.activmitsu_can.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
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
import androidx.compose.ui.unit.dp
import com.example.activmitsu_can.R
import com.example.activmitsu_can.utils.collectAsEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    state: MainState,
    onTryToConnect: (Boolean) -> Unit,
    onTryToDisconnect: () -> Unit,
    onDeviceIdChange: (String) -> Unit,
    onGoToSettings: () -> Unit,
    onChangeOverlayNeeded: (Boolean) -> Unit,
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
    Box {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(0.3F)
            ) {
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
                    onClick = { onTryToConnect.invoke(state.isNeedOverlay) },
                    enabled = !state.isConnected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text(text = stringResource(id = R.string.try_to_connect))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = state.isNeedOverlay,
                        onCheckedChange = onChangeOverlayNeeded,
                        enabled = !state.isConnected
                    )
                    Text(
                        text = stringResource(id = R.string.is_foreground_is_needed)
                    )
                }
                Button(
                    onClick = onTryToDisconnect,
                    enabled = state.isConnected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text(text = stringResource(id = R.string.try_to_disconnect))
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(0.6F)
            ) {

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