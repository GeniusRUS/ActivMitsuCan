package com.example.activmitsu_can.ui.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activmitsu_can.domain.can.DEVICE_ID
import com.example.activmitsu_can.domain.can.DISPLAYED_DELAY
import com.example.activmitsu_can.domain.can.ICanCommon
import com.example.activmitsu_can.domain.can.LOW_PRESSURE
import com.example.activmitsu_can.domain.can.TYRE_SENSOR_LF
import com.example.activmitsu_can.domain.can.TYRE_SENSOR_LR
import com.example.activmitsu_can.domain.can.TYRE_SENSOR_RF
import com.example.activmitsu_can.domain.can.TYRE_SENSOR_RR
import com.example.activmitsu_can.utils.extendedCombine
import com.ub.utils.withUseCaseScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update

class PreferencesViewModel(
    canCommon: ICanCommon,
    private val savedStateHandle: SavedStateHandle,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _state = MutableStateFlow(PreferencesState())
    val state = _state.asStateFlow()

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    private val _dataSavedFlow = MutableSharedFlow<Unit>()
    val dataSavedFlow = _dataSavedFlow.asSharedFlow()

    val devicesFlow = canCommon.commonState

    init {
        extendedCombine(
            savedStateHandle.getStateFlow(DEVICE_ID, _state.value.deviceId),
            savedStateHandle.getStateFlow(TYRE_SENSOR_LF, _state.value.sensorLeftFront),
            savedStateHandle.getStateFlow(TYRE_SENSOR_RF, _state.value.sensorRightFront),
            savedStateHandle.getStateFlow(TYRE_SENSOR_LR, _state.value.sensorLeftRear),
            savedStateHandle.getStateFlow(TYRE_SENSOR_RR, _state.value.sensorRightRear),
            savedStateHandle.getStateFlow(LOW_PRESSURE, _state.value.lowPressureTreshhold),
            savedStateHandle.getStateFlow(DISPLAYED_DELAY, _state.value.displayingHideDelayInSeconds),
        ) { deviceId, leftFront, rightFront, leftRear, rightRear, lowPressure, displayedDelay ->
            _state.update { state ->
                state.copy(
                    deviceId = deviceId,
                    sensorLeftFront = leftFront,
                    sensorRightFront = rightFront,
                    sensorLeftRear = leftRear,
                    sensorRightRear = rightRear,
                    lowPressureTreshhold = lowPressure,
                    displayingHideDelayInSeconds = displayedDelay
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onDeviceIdChanged(deviceId: String) {
        savedStateHandle[DEVICE_ID] = deviceId
    }

    fun onLeftFrontChange(leftFront: String) {
        savedStateHandle[TYRE_SENSOR_LF] = leftFront
    }

    fun onRightFrontChange(rightFront: String) {
        savedStateHandle[TYRE_SENSOR_RF] = rightFront
    }

    fun onLeftRearChange(leftRear: String) {
        savedStateHandle[TYRE_SENSOR_LR] = leftRear
    }

    fun onRightRearChange(rightRear: String) {
        savedStateHandle[TYRE_SENSOR_RR] = rightRear
    }

    fun onLowPressureChange(lowPressure: String) {
        savedStateHandle[LOW_PRESSURE] = lowPressure.toFloatOrNull()
    }

    fun onDisplayDelayChange(displayDelay: String) {
        savedStateHandle[DISPLAYED_DELAY] = displayDelay.toIntOrNull()
    }

    fun saveData() {
        withUseCaseScope {
            val deviceId = _state.value.deviceId.toIntOrNull() ?: run {
                _errorFlow.emit("Device id is not correct")
                return@withUseCaseScope
            }
            val leftFront = _state.value.sensorLeftFront.toIntOrNull() ?: run {
                _errorFlow.emit("Left front is not correct")
                return@withUseCaseScope
            }
            val rightFront = _state.value.sensorRightFront.toIntOrNull() ?: run {
                _errorFlow.emit("Right front is not correct")
                return@withUseCaseScope
            }
            val leftRear = _state.value.sensorLeftRear.toIntOrNull() ?: run {
                _errorFlow.emit("Left rear is not correct")
                return@withUseCaseScope
            }
            val rightRear = _state.value.sensorRightRear.toIntOrNull() ?: run {
                _errorFlow.emit("Right rear is not correct")
                return@withUseCaseScope
            }
            dataStore.updateData {
                it.toMutablePreferences().apply {
                    this[intPreferencesKey(DEVICE_ID)] = deviceId
                    this[intPreferencesKey(TYRE_SENSOR_LF)] = leftFront
                    this[intPreferencesKey(TYRE_SENSOR_RF)] = rightFront
                    this[intPreferencesKey(TYRE_SENSOR_LR)] = leftRear
                    this[intPreferencesKey(TYRE_SENSOR_RR)] = rightRear
                    this[floatPreferencesKey(LOW_PRESSURE)] = _state.value.lowPressureTreshhold
                    this[intPreferencesKey(DISPLAYED_DELAY)] = _state.value.displayingHideDelayInSeconds
                }
            }
            _dataSavedFlow.emit(Unit)
        }
    }
}