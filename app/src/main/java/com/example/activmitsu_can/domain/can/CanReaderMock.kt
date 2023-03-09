package com.example.activmitsu_can.domain.can

import com.ub.utils.timer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Random
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class CanReaderMock @Inject constructor(
    @Named(value = "globalScope") private val appCoroutineScope: CoroutineScope
) : ICanReader, ICanCommon {

    private val _readerState = MutableStateFlow(CanStateModel())
    override val readerState: StateFlow<CanStateModel>
        get() = _readerState.asStateFlow()

    private val _commonState = MutableStateFlow(CanCommonState())
    override val commonState: StateFlow<CanCommonState>
        get() = _commonState.asStateFlow()

    init {
        appCoroutineScope.launch {
            timer.forEach { time ->
                delay(TimeUnit.SECONDS.toMillis(10))
                _commonState.update { state ->
                    state.copy(
                        availableDevices = listOf(
                            CanDevice(
                                name = "name",
                                deviceId = 0,
                                productName = "product name",
                                vendorId = 1,
                                productId = 2
                            )
                        )
                    )
                }
                if (_commonState.value.isConnected) {
                    _readerState.update { state ->
                        val random = Random()
                        state.copy(
                            speed = random.nextInt(),
                            cvtTemp = random.nextInt(),
                            openable = state.openable.copy(
                                leftForward = random.nextBoolean(),
                                leftBackward = random.nextBoolean(),
                                rightBackward = random.nextBoolean(),
                                rightForward = random.nextBoolean(),
                                truncate = random.nextBoolean(),
                                hood = random.nextBoolean()
                            ),
                            wheels = state.wheels.copy(
                                leftFrontPressure = random.nextInt(25).div(10F),
                                leftRearPressure = random.nextInt(25).div(10F),
                                rightFrontPressure = random.nextInt(25).div(10F),
                                rightRearPressure = random.nextInt(25).div(10F),
                                leftFrontTemperature = random.nextInt(20),
                                leftRearTemperature = random.nextInt(20),
                                rightFrontTemperature = random.nextInt(20),
                                rightRearTemperature = random.nextInt(20),
                            )
                        )
                    }
                } else {
                    _commonState.update { state ->
                        val devicesCount = Random().nextInt(5)
                        val devices = buildList {
                            (0..devicesCount).forEach { position ->
                                add(
                                    CanDevice(
                                        name = "Device $position",
                                        deviceId = position,
                                        productName = "Name for $position",
                                        vendorId = position,
                                        productId = position
                                    )
                                )
                            }
                        }
                        state.copy(
                            availableDevices = devices
                        )
                    }
                }
            }
        }
    }

    override fun tryToConnect() {
        _commonState.update { state ->
            state.copy(
                isConnected = true,
            )
        }
    }

    override fun tryToDisconnect() {
        _readerState.update { state ->
            state.copy(
                speed = 0,
                cvtTemp = 0
            )
        }
        _commonState.update { state ->
            state.copy(
                isConnected = false
            )
        }
    }

    override fun attachListener() = Unit

    override fun detachListener() = Unit

    override fun setDeviceId(deviceId: Int) = Unit
}