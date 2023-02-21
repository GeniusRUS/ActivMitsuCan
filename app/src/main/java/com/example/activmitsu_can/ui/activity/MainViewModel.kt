package com.example.activmitsu_can.ui.activity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activmitsu_can.domain.can.ICanReader
import com.ub.utils.withUseCaseScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainViewModel(
    private val canReader: ICanReader
) : ViewModel() {

    val state = canReader.state

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    init {
        canReader.state.onEach {

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

    fun propagateError(message: String) {
        withUseCaseScope {
            _errorFlow.emit(message)
        }
    }
}