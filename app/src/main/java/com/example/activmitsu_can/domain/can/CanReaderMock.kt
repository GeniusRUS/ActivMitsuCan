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

class CanReaderMock @Inject constructor(
    @Named(value = "globalScope") private val appCoroutineScope: CoroutineScope
): ICanReader {

    private val _state = MutableStateFlow(CanStateModel())
    override val state: StateFlow<CanStateModel>
        get() = _state.asStateFlow()

    override fun attachListener() {
    }

    override fun tryToConnect() {
        appCoroutineScope.launch {
            timer.forEach { time ->
                delay(TimeUnit.SECONDS.toMillis(1))
                _state.update { state ->
                    state.copy(
                        data = Random().nextInt(4).toString(),
                        status = time.toString()
                    )
                }
            }
        }
    }

    override fun detachListener() {
    }
}