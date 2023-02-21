package com.example.activmitsu_can.domain.can

import kotlinx.coroutines.flow.StateFlow

interface ICanReader {
    val readerState: StateFlow<CanStateModel>
    fun tryToConnect()
    fun tryToDisconnect()
    fun attachListener()
    fun detachListener()
}