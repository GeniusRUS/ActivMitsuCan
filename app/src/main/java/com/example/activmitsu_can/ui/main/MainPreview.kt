package com.example.activmitsu_can.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.activmitsu_can.ui.theme.ActivMitsuCanTheme
import kotlinx.coroutines.flow.emptyFlow

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ActivMitsuCanTheme {
        MainScreen(
            state = MainState(),
            onTryToConnect = {},
            onTryToDisconnect = {},
            onDeviceIdChange = {},
            errorFlow = emptyFlow()
        )
    }
}