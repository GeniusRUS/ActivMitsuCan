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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.IOException
import javax.inject.Inject

class CanReaderImpl @Inject constructor(
    private val context: Context
) : ICanReader, SerialInputOutputManager.Listener {

    private val _state = MutableStateFlow(CanStateModel())
    override val state: StateFlow<CanStateModel> = _state.asStateFlow()

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

    override fun tryToConnect() {
        connect()
    }

    override fun attachListener() {
        context.registerReceiver(broadcastReceiver, IntentFilter(INTENT_ACTION_GRANT_USB))
    }

    override fun detachListener() {
        context.unregisterReceiver(broadcastReceiver)
    }

    override fun onNewData(data: ByteArray?) {
        _state.update { state ->
            state.copy(
                data = data.toString()
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
            status("connection failed: device not found")
            return
        }
        val driver: UsbSerialDriver? = UsbSerialProber.getDefaultProber().probeDevice(device)
        if (driver == null) {
            status("connection failed: no driver for device")
            return
        }
        if (driver.ports.size < portNum) {
            status("connection failed: not enough ports at device")
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
            if (manager?.hasPermission(driver.device) == true) {
                status("connection failed: permission denied")
            } else {
                status("connection failed: open failed")
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
            status("connected")
            connected = true
        } catch (e: Exception) {
            status("connection failed: " + e.message)
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

    private fun status(info: String) {
        _state.update { state ->
            state.copy(
                status = info
            )
        }
    }

    companion object {
        private const val TAG = "CanReader"
        const val DEVICE_ATTACHED_SIGNAL = "android.hardware.usb.action.USB_DEVICE_ATTACHED"
        const val INTENT_ACTION_GRANT_USB: String = BuildConfig.APPLICATION_ID + ".GRANT_USB"
    }
}