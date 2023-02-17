package com.example.activmitsu_can.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.activmitsu_can.di.DIManager
import com.example.activmitsu_can.domain.can.CanReaderImpl.Companion.DEVICE_ATTACHED_SIGNAL
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
            ActivMitsuCanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
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
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ActivMitsuCanTheme {
        Greeting("Android")
    }
}