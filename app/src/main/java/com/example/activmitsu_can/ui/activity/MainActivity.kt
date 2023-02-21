package com.example.activmitsu_can.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.activmitsu_can.R
import com.example.activmitsu_can.di.DIManager
import com.example.activmitsu_can.domain.can.CanReaderImpl.Companion.DEVICE_ATTACHED_SIGNAL
import com.example.activmitsu_can.domain.can.CanStateModel
import com.example.activmitsu_can.domain.service.OverflowWindowService
import com.example.activmitsu_can.ui.theme.ActivMitsuCanTheme
import com.example.activmitsu_can.utils.collectAsEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels {
        DIManager.appComponent.viewModelProvider
    }

    private val notificationPermissionCaller = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            createOverlayWindow()
        }
    }

    private val drawOverResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (Settings.canDrawOverlays(this) && checkNotificationPermission()) {
            createOverlayWindow()
        } else {
            viewModel.propagateError(
                getString(R.string.error_permission_overlay_not_granted)
            )
        }
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
                    MainScreen(
                        data = state.data ?: "n/a",
                        status = state.status ?: "n/a",
                        isConnected = state.status != null,
                        onTryToConnect = { viewModel.startListener() },
                        errorFlow = viewModel.errorFlow
                    )
                }
            }
        }

        if (checkStartPermissionRequest() && checkNotificationPermission()) {
            createOverlayWindow()
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

    private fun checkStartPermissionRequest(): Boolean {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            drawOverResult.launch(intent)
            return false
        }
        return true
    }

    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            if (!isGranted) {
                notificationPermissionCaller.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            isGranted
        } else {
            true
        }
    }

    private fun createOverlayWindow() {
        val intent = Intent(this, OverflowWindowService::class.java)
            .setAction(OverflowWindowService.START_SERVICE)
        startService(intent)
    }
}

@Composable
fun MainScreen(
    data: String,
    status: String,
    isConnected: Boolean,
    onTryToConnect: () -> Unit,
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
                text = "Data: $data\nStatus: $status"
            )
            Button(
                onClick = onTryToConnect,
                enabled = !isConnected
            ) {
                Text(text = stringResource(id = R.string.try_to_connect))
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ActivMitsuCanTheme {
        MainScreen(
            "Android",
            "null",
            isConnected = false,
            onTryToConnect = {},
            emptyFlow()
        )
    }
}