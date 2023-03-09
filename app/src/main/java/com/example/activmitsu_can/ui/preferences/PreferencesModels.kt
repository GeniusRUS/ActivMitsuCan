package com.example.activmitsu_can.ui.preferences

data class PreferencesState(
    val deviceId: String = "",
    val sensorLeftFront: String = "",
    val sensorLeftRear: String = "",
    val sensorRightFront: String = "",
    val sensorRightRear: String = "",
    val lowPressureTreshhold: Float = 2F,
    val displayingHideDelayInSeconds: Int = 5
)