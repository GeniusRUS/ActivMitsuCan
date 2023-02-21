package com.example.activmitsu_can.domain.can

import kotlinx.coroutines.flow.StateFlow

interface ICanCommon {
    val commonState: StateFlow<CanCommonState>
    fun setDeviceId(deviceId: Int)
}