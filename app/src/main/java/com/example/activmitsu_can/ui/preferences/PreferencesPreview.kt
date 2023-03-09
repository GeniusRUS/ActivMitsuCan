package com.example.activmitsu_can.ui.preferences

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.activmitsu_can.domain.can.CanCommonState
import com.example.activmitsu_can.ui.theme.ActivMitsuCanTheme
import kotlinx.coroutines.flow.emptyFlow

@Preview(showBackground = true, widthDp = 1280, heightDp = 720)
@Composable
fun DefaultPreview() {
    ActivMitsuCanTheme {
        PreferencesScreen(
            state = PreferencesState(),
            devices = CanCommonState(),
            errorFlow = emptyFlow(),
            onSave = {},
            onRightRearChange = {},
            onRightFrontChange = {},
            onLeftRearChange = {},
            onLeftFrontChange = {},
            onDeviceIdChange = {},
            onSelectDeviceToConnect = {}
        )
    }
}