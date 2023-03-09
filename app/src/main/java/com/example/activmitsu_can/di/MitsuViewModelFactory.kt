package com.example.activmitsu_can.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.activmitsu_can.domain.can.ICanCommon
import com.example.activmitsu_can.domain.can.ICanReader
import com.example.activmitsu_can.ui.activity.ApplicationViewModel
import com.example.activmitsu_can.ui.preferences.PreferencesViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MitsuViewModelFactory @Inject constructor(
    private val context: Context,
    private val canCommon: ICanCommon,
    private val canReader: ICanReader,
    private val dataStore: DataStore<Preferences>
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when (modelClass) {
            ApplicationViewModel::class.java -> ApplicationViewModel(canReader)
            PreferencesViewModel::class.java -> PreferencesViewModel(canCommon, extras.createSavedStateHandle(), dataStore)
            else -> throw IllegalArgumentException("Unknown class $modelClass")
        } as T
    }
}