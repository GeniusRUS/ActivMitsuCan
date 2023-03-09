package com.example.activmitsu_can.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.activmitsu_can.R
import com.example.activmitsu_can.di.DIManager
import com.example.activmitsu_can.domain.service.OverflowWindowService
import com.example.activmitsu_can.ui.theme.ActivMitsuCanTheme
import com.example.activmitsu_can.utils.provideFactory

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels {
        provideFactory {
            DIManager.appComponent.mainViewModelFactory.create(it)
        }
    }

    private val notificationPermissionCaller = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            createOverlayWindow()
        }
    }

    private val drawOverResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (Settings.canDrawOverlays(requireContext()) && checkNotificationPermission()) {
            createOverlayWindow()
        } else {
            viewModel.propagateError(getString(R.string.error_permission_overlay_not_granted))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(inflater.context).apply {
            setContent {
                val state: MainState by viewModel.state.collectAsState()
                ActivMitsuCanTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        MainScreen(
                            state = state,
                            onTryToConnect = { isNeedOverlay ->
                                if (isNeedOverlay) {
                                    if (checkStartPermissionRequest() && checkNotificationPermission()) {
                                        createOverlayWindow()
                                        viewModel.startListener()
                                    }
                                } else {
                                    viewModel.startListener()
                                }
                            },
                            onTryToDisconnect = { isNeedOverlay ->
                                if (isNeedOverlay) {
                                    removeOverlayWindow()
                                }
                                viewModel.stopListener()
                            },
                            onGoToSettings = {
                                val direction = MainFragmentDirections.actionMainFragmentToPreferencesFragment()
                                findNavController().navigate(direction)
                            },
                            onChangeOverlayNeeded = { viewModel.onChangeOverlayNeeded(it) },
                            errorFlow = viewModel.errorFlow
                        )
                    }
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

    private fun checkStartPermissionRequest(): Boolean {
        if (!Settings.canDrawOverlays(requireContext())) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${requireContext().packageName}")
            )
            drawOverResult.launch(intent)
            return false
        }
        return true
    }

    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isGranted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            if (!isGranted) {
                notificationPermissionCaller.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            isGranted
        } else {
            true
        }
    }

    private fun createOverlayWindow() {
        val intent = Intent(requireContext(), OverflowWindowService::class.java)
            .setAction(OverflowWindowService.START_SERVICE)
        requireActivity().startService(intent)
    }

    private fun removeOverlayWindow() {
        val intent = Intent(requireContext(), OverflowWindowService::class.java)
            .setAction(OverflowWindowService.STOP_SERVICE)
        requireActivity().startService(intent)
    }
}