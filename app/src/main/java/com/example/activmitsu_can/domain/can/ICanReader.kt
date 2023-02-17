package com.example.activmitsu_can.domain.can

import kotlinx.coroutines.flow.StateFlow

interface ICanReader {
    val state: StateFlow<CanStateModel>
    fun tryToConnect()
    fun attachListener()
    fun detachListener()
}