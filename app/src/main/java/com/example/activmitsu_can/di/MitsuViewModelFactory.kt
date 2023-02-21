package com.example.activmitsu_can.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.activmitsu_can.domain.can.ICanReader
import com.example.activmitsu_can.ui.activity.ApplicationViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MitsuViewModelFactory @Inject constructor(
    private val context: Context,
    private val canReader: ICanReader
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when (modelClass) {
            ApplicationViewModel::class.java -> ApplicationViewModel(canReader)
            else -> throw IllegalArgumentException("Unknown class $modelClass")
        } as T
    }
}