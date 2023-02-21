package com.example.activmitsu_can.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.activmitsu_can.R
import com.example.activmitsu_can.di.DIManager
import com.example.activmitsu_can.domain.can.CanReaderImpl.Companion.DEVICE_ATTACHED_SIGNAL

class ApplicationActivity : AppCompatActivity(R.layout.activity_application) {

    private val viewModel: ApplicationViewModel by viewModels {
        DIManager.appComponent.viewModelProvider
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (intent?.action == DEVICE_ATTACHED_SIGNAL) {
            viewModel.startListener()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == DEVICE_ATTACHED_SIGNAL) {
            viewModel.startListener()
        }
    }
}