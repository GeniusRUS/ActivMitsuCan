package com.example.activmitsu_can.ui.preferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.activmitsu_can.di.DIManager
import com.example.activmitsu_can.domain.can.CanCommonState
import com.example.activmitsu_can.ui.theme.ActivMitsuCanTheme
import com.example.activmitsu_can.utils.collectAsEffect

class PreferencesFragment : Fragment() {

    private val viewModel: PreferencesViewModel by viewModels {
        DIManager.appComponent.viewModelProvider
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(inflater.context).apply {
            setContent {
                val state: PreferencesState by viewModel.state.collectAsState()
                viewModel.dataSavedFlow.collectAsEffect { findNavController().popBackStack() }
                val devices: CanCommonState by viewModel.devicesFlow.collectAsState()
                ActivMitsuCanTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        PreferencesScreen(
                            state = state,
                            devices = devices,
                            onDeviceIdChange = { viewModel.onDeviceIdChanged(it) },
                            onLeftFrontChange = { viewModel.onLeftFrontChange(it) },
                            onLeftRearChange = { viewModel.onLeftRearChange(it) },
                            onRightFrontChange = { viewModel.onRightFrontChange(it) },
                            onRightRearChange = { viewModel.onRightRearChange(it) },
                            onSave = { viewModel.saveData() },
                            onSelectDeviceToConnect = { viewModel.onDeviceIdChanged(it.toString()) },
                            errorFlow = viewModel.errorFlow
                        )
                    }
                }
            }
        }
    }
}