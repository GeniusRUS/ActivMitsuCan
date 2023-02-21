package com.example.activmitsu_can.ui.main

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activmitsu_can.domain.can.DEVICE_ID
import com.example.activmitsu_can.domain.can.ICanCommon
import com.example.activmitsu_can.domain.can.ICanReader
import com.ub.utils.withUseCaseScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@AssistedFactory
interface MainViewModelFactory {
    fun create(savedStateHandle: SavedStateHandle): MainViewModel
}

class MainViewModel @AssistedInject constructor(
    private val canCommon: ICanCommon,
    private val canReader: ICanReader,
    private val dataStore: DataStore<Preferences>,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    init {
        dataStore.data.map { it[intPreferencesKey(DEVICE_ID)] }.onEach { savedDeviceId ->
            savedStateHandle[DEVICE_ID] = savedDeviceId?.toString() ?: ""
        }.launchIn(viewModelScope)
        combine(
            savedStateHandle.getStateFlow(DEVICE_ID, ""),
            canReader.readerState,
            canCommon.commonState
        ) { deviceId, readerState, commonState ->
            _state.update { state ->
                state.copy(
                    deviceId = deviceId,
                    speed = readerState.speed,
                    cvtTemp = readerState.cvtTemp,
                    isConnected = commonState.isConnected
                )
            }
        }.launchIn(viewModelScope)
    }

    fun stopListener() {
        canReader.tryToDisconnect()
    }

    fun startListener() {
        withUseCaseScope {
            _state.value.deviceId.toIntOrNull()?.let { deviceId ->
                dataStore.updateData {
                    it.toMutablePreferences().apply {
                        this[intPreferencesKey(DEVICE_ID)] = deviceId
                    }
                }
                canCommon.setDeviceId(deviceId)
                canReader.tryToConnect()
            } ?: _errorFlow.emit("Device id is not correct")
        }
    }

    fun onDeviceIdChanged(deviceId: String) {
        savedStateHandle[DEVICE_ID] = deviceId
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