package com.example.activmitsu_can.ui.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.activmitsu_can.R
import com.example.activmitsu_can.domain.can.CanCommonState
import com.example.activmitsu_can.utils.collectAsEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun PreferencesScreen(
    state: PreferencesState,
    devices: CanCommonState,
    onDeviceIdChange: (String) -> Unit,
    onLeftFrontChange: (String) -> Unit,
    onRightFrontChange: (String) -> Unit,
    onLeftRearChange: (String) -> Unit,
    onLowPressureChange: (String) -> Unit,
    onDisplayingHideChange: (String) -> Unit,
    onRightRearChange: (String) -> Unit,
    onSelectDeviceToConnect: (Int) -> Unit,
    onSave: () -> Unit,
    errorFlow: Flow<String>,
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
    Box(
        modifier = Modifier.statusBarsPadding()
    ) {
        val scrollState = rememberScrollState()
        Row {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
            ) {
                OutlinedTextField(
                    value = state.deviceId,
                    onValueChange = onDeviceIdChange,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    placeholder = {
                        Text(text = stringResource(id = R.string.hint_device_id))
                    }
                )
                Row {
                    OutlinedTextField(
                        value = state.sensorLeftFront,
                        onValueChange = onLeftFrontChange,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        placeholder = {
                            Text(text = stringResource(id = R.string.description_wheel_front_left))
                        }
                    )
                    OutlinedTextField(
                        value = state.sensorRightFront,
                        onValueChange = onRightFrontChange,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        placeholder = {
                            Text(text = stringResource(id = R.string.description_wheel_front_right))
                        }
                    )
                }
                Row {
                    OutlinedTextField(
                        value = state.sensorLeftRear,
                        onValueChange = onLeftRearChange,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        placeholder = {
                            Text(text = stringResource(id = R.string.description_wheel_rear_left))
                        }
                    )
                    OutlinedTextField(
                        value = state.sensorRightRear,
                        onValueChange = onRightRearChange,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        placeholder = {
                            Text(text = stringResource(id = R.string.description_wheel_rear_right))
                        }
                    )
                }
                Row {
                    OutlinedTextField(
                        value = state.lowPressureTreshhold.toString(),
                        onValueChange = onLowPressureChange,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        placeholder = {
                            Text(text = stringResource(id = R.string.low_pressure_treshhold))
                        }
                    )
                    OutlinedTextField(
                        value = state.displayingHideDelayInSeconds.toString(),
                        onValueChange = onDisplayingHideChange,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        placeholder = {
                            Text(text = stringResource(id = R.string.displaying_hide_delay))
                        }
                    )
                }
                Button(onClick = onSave) {
                    Text(text = stringResource(id = R.string.save))
                }
            }
            if (!devices.isConnected) {
                Column(
                    modifier = Modifier
                        .weight(1F)
                ) {
                    Text(
                        text = stringResource(id = R.string.preferences_devices_title),
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    LazyColumn(
                        content = {
                            items(
                                count = devices.availableDevices.size,
                                key = { position -> devices.availableDevices[position].deviceId }
                            ) { position ->
                                val device = devices.availableDevices[position]
                                DeviceToConnect(
                                    name = device.name,
                                    onSelectDevice = { onSelectDeviceToConnect.invoke(device.deviceId) }
                                )
                            }
                        },
                        contentPadding = PaddingValues(
                            horizontal = 8.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
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

@Composable
fun DeviceToConnect(
    name: String,
    onSelectDevice: () -> Unit
) {
    Text(
        text = name,
        modifier = Modifier
            .clickable(onClick = onSelectDevice)
            .fillMaxWidth()
            .padding(16.dp)
    )
}