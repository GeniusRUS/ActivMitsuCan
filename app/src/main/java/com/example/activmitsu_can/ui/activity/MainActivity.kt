package com.example.activmitsu_can.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.activmitsu_can.di.DIManager
import com.example.activmitsu_can.domain.can.CanReaderImpl.Companion.DEVICE_ATTACHED_SIGNAL
import com.example.activmitsu_can.domain.can.CanStateModel
import com.example.activmitsu_can.ui.theme.ActivMitsuCanTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        DIManager.appComponent.viewModelProvider
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent?.action == DEVICE_ATTACHED_SIGNAL) {
            viewModel.startListener()
        }
        setContent {
            val state: CanStateModel by viewModel.state.collectAsState()
            ActivMitsuCanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(state.data ?: "n/a", state.status ?: "n/a")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.attachListener()
    }

    override fun onPause() {
        super.onPause()
        viewModel.detachListener()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == DEVICE_ATTACHED_SIGNAL) {
            viewModel.startListener()
        }
    }
}

@Composable
fun MainScreen(data: String, status: String) {
    Box(contentAlignment = Alignment.Center) {
        Text(
            text = "Data: $data\nStatus: $status"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ActivMitsuCanTheme {
        MainScreen("Android", "null")
    }
}