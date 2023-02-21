package com.example.activmitsu_can.domain.can

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.example.activmitsu_can.BuildConfig
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import com.ub.utils.timer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class CanReaderImpl @Inject constructor(
    private val context: Context,
    @Named(value = "globalScope") private val appCoroutineScope: CoroutineScope
) : ICanReader, ICanCommon, SerialInputOutputManager.Listener {

    private val _readerState = MutableStateFlow(CanStateModel())
    override val readerState: StateFlow<CanStateModel> = _readerState.asStateFlow()

    private val _commonState = MutableStateFlow(CanCommonState())
    override val commonState: StateFlow<CanCommonState>
        get() = _commonState.asStateFlow()

    private enum class UsbPermission {
        Unknown, Requested, Granted, Denied
    }

    private var connected: Boolean = false
    private var withIoManager: Boolean = false
    private var deviceId: Int = 0
    private var portNum: Int = 0
    private var baudRate: Int = 0
    private var usbIoManager: SerialInputOutputManager? = null
    private var usbSerialPort: UsbSerialPort? = null
    private var usbPermission = UsbPermission.Unknown
    private val manager = getSystemService(context, UsbManager::class.java)
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (INTENT_ACTION_GRANT_USB == intent?.action) {
                usbPermission =
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        UsbPermission.Granted
                    } else UsbPermission.Denied
                connect()
            }
        }
    }

    init {
        appCoroutineScope.launch {
            timer.forEach { time ->
                delay(TimeUnit.SECONDS.toMillis(1))
                _commonState.update { state ->
                    state.copy(
                        availableDevices = (manager?.deviceList?.values ?: listOf()).toMutableList().map { device ->
                            CanDevice(
                                deviceId = device.deviceId,
                                productName = device.productName,
                                vendorId = device.vendorId,
                                productId = device.productId
                            )
                        }
                    )
                }
                if (connected) {
                    read()
                }
            }
        }
    }

    override fun tryToConnect() {
        connect()
    }

    override fun tryToDisconnect() {
        disconnect()
    }

    override fun attachListener() {
        context.registerReceiver(broadcastReceiver, IntentFilter(INTENT_ACTION_GRANT_USB))
    }

    override fun detachListener() {
        context.unregisterReceiver(broadcastReceiver)
    }

    override fun setDeviceId(deviceId: Int) {
        this.deviceId = deviceId
    }

    override fun onNewData(data: ByteArray?) {
        _readerState.update { state ->
            state.copy(

            )
        }
    }

    override fun onRunError(e: java.lang.Exception) {
        Log.e(TAG, e.message, e)
    }

    private fun connect() {
        var device: UsbDevice? = null
        for (v in manager?.deviceList?.values ?: listOf()) {
            if (v.deviceId == deviceId) device = v
        }
        if (device == null) {
            status("connection failed: device not found", false)
            return
        }
        val driver: UsbSerialDriver? = UsbSerialProber.getDefaultProber().probeDevice(device)
        if (driver == null) {
            status("connection failed: no driver for device", false)
            return
        }
        if (driver.ports.size < portNum) {
            status("connection failed: not enough ports at device", false)
            return
        }
        usbSerialPort = driver.ports[portNum]
        val usbConnection: UsbDeviceConnection? = manager?.openDevice(driver.device)
        if (usbConnection == null
            && usbPermission == UsbPermission.Unknown
            && manager?.hasPermission(driver.device) == false
        ) {
            usbPermission = UsbPermission.Requested
            val flags = PendingIntent.FLAG_IMMUTABLE
            val usbPermissionIntent: PendingIntent =
                PendingIntent.getBroadcast(context, 0, Intent(INTENT_ACTION_GRANT_USB), flags)
            manager.requestPermission(driver.device, usbPermissionIntent)
            return
        }
        if (usbConnection == null) {
            if (manager?.hasPermission(driver.device) == false) {
                status("connection failed: permission denied", false)
            } else {
                status("connection failed: open failed", false)
            }
            return
        }
        try {
            usbSerialPort?.open(usbConnection)
            usbSerialPort?.setParameters(baudRate, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
            if (withIoManager) {
                usbIoManager = SerialInputOutputManager(usbSerialPort, this)
                usbIoManager?.start()
            }
            status("connected", true)
            connected = true
        } catch (e: Exception) {
            status("connection failed: ${e.message}", false)
            disconnect()
        }
    }

    private fun disconnect() {
        connected = false
        if (usbIoManager != null) {
            usbIoManager?.listener = null
            usbIoManager?.stop()
        }
        usbIoManager = null
        try {
            usbSerialPort?.close()
        } catch (ignored: IOException) {
        }
        usbSerialPort = null
    }

    private fun read() {
        if (!connected) return
        try {
            val buffer = ByteArray(8192)
            val byteResult = mutableListOf<Byte>()
            while (usbSerialPort?.read(buffer, READ_WAIT_MILLIS) != 0) {
                byteResult.addAll(buffer.toList())
            }
            println("Read size: ${byteResult.size}\nBytes: $byteResult")
            _readerState.update { state ->
                state.copy(

                )
            }
        } catch (e: IOException) {
            // when using read with timeout, USB bulkTransfer returns -1 on timeout _and_ errors
            // like connection loss, so there is typically no exception thrown here on error
            status("connection lost: ${e.message}", false)
            disconnect()
        }
    }

    private fun status(info: String, isConnected: Boolean) {
        _readerState.update { state ->
            state.copy(

            )
        }
        _commonState.update { state ->
            state.copy(
                isConnected = isConnected
            )
        }
    }

    companion object {
        private const val TAG = "CanReader"
        private const val READ_WAIT_MILLIS = 2000
        const val DEVICE_ATTACHED_SIGNAL = "android.hardware.usb.action.USB_DEVICE_ATTACHED"
        const val INTENT_ACTION_GRANT_USB: String = BuildConfig.APPLICATION_ID + ".GRANT_USB"
    }
}