package com.example.activmitsu_can.domain.can

data class CanStateModel(
    val openable: CanOpenableState = CanOpenableState(),
    val speed: Int = 0,
    val wheels: WheelState = WheelState(),
    val engineTemp: Int = 0,
    val cvtTemp: Int = 0
)

data class CanOpenableState(
    val leftForward: Boolean = false,
    val rightForward: Boolean = false,
    val leftBackward: Boolean = false,
    val rightBackward: Boolean = false,
    val truncate: Boolean = false,
    val hood: Boolean = false
)

data class WheelState(
    val leftFrontPressure: Float = 0F,
    val rightFrontPressure: Float = 0F,
    val leftRearPressure: Float = 0F,
    val rightRearPressure: Float = 0F,
    val leftFrontTemperature: Int = 0,
    val rightFrontTemperature: Int = 0,
    val leftRearTemperature: Int = 0,
    val rightRearTemperature: Int = 0,
)

data class CanCommonState(
    val isConnected: Boolean = false,
    val availableDevices: List<CanDevice> = listOf()
)

data class CanDevice(
    val name: String,
    val deviceId: Int,
    val productName: String?,
    val vendorId: Int,
    val productId: Int
)

const val DEVICE_ID = "device_id"
const val TYRE_SENSOR_LF = "sensor_lf"  // left forward
const val TYRE_SENSOR_RF = "sensor_rf"  // right forward
const val TYRE_SENSOR_LB = "sensor_lb"  // left backward
const val TYRE_SENSOR_RB = "sensor_rb"  // right backward