package com.example.activmitsu_can.ui.activity

import androidx.lifecycle.ViewModel
import com.example.activmitsu_can.domain.can.ICanReader
import com.ub.utils.withUseCaseScope

class ApplicationViewModel(
    private val canReader: ICanReader
): ViewModel() {

    fun startListener() {
        withUseCaseScope {
            canReader.tryToConnect()
        }
    }
}