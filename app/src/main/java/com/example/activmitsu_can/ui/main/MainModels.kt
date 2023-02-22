package com.example.activmitsu_can.ui.main

data class MainState(
    val deviceId: String = "",
    val speed: Int = 0,
    val cvtTemp: Int = 0,
    val isConnected: Boolean = false,
    val isNeedOverlay: Boolean = false
)