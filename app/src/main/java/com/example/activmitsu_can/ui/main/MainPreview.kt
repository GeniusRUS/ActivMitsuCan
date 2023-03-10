package com.example.activmitsu_can.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.activmitsu_can.ui.theme.ActivMitsuCanTheme
import kotlinx.coroutines.flow.emptyFlow

@Preview(showBackground = true, widthDp = 1280, heightDp = 720)
@Composable
fun DefaultPreview() {
    ActivMitsuCanTheme {
        MainScreen(
            state = MainState(),
            onTryToConnect = {},
            onTryToDisconnect = {},
            onGoToSettings = {},
            onChangeOverlayNeeded = {},
            errorFlow = emptyFlow()
        )
    }
}