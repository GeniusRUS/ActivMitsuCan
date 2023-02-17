package com.example.activmitsu_can.ui.activity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activmitsu_can.domain.can.ICanReader
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainViewModel(
    private val canReader: ICanReader
) : ViewModel() {

    init {
        canReader.state.onEach {
            Log.d("CAN", it.toString())
        }.launchIn(viewModelScope)
    }

    fun startListener() {
        canReader.tryToConnect()
    }

    fun attachListener() {
        canReader.attachListener()
    }

    fun detachListener() {
        canReader.detachListener()
    }
}