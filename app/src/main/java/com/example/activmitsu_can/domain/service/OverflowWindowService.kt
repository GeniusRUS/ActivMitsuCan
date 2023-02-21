package com.example.activmitsu_can.domain.service

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.activmitsu_can.BuildConfig
import com.example.activmitsu_can.R
import com.example.activmitsu_can.di.DIManager
import com.example.activmitsu_can.domain.can.ICanReader
import com.example.activmitsu_can.ui.activity.MainActivity
import com.ub.utils.LogUtils
import com.ub.utils.UbNotify
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import javax.inject.Inject


class OverflowWindowService : Service() {

    @Inject
    lateinit var canReader: ICanReader

    private val superJob = SupervisorJob()
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO) + superJob

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        LogUtils.e(TAG, exception.message, exception)
    }

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var params: LayoutParams? = null
    private var handler: Handler? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        DIManager.appComponent.inject(this)

        handler = Handler(Looper.getMainLooper())

        windowManager = ContextCompat.getSystemService(this, WindowManager::class.java)
        params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.TYPE_APPLICATION_OVERLAY,
                LayoutParamFlags,
                PixelFormat.TRANSLUCENT
            )
        } else {
            @Suppress("DEPRECATION")
            LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.TYPE_PHONE,
                LayoutParamFlags,
                PixelFormat.TRANSLUCENT
            )
        }
        params?.gravity = Gravity.CENTER
        overlayView = View.inflate(this, R.layout.overlay_view, null)

        overlayView?.setOnTouchListener(
            object : OnTouchListener {
                val floatWindowLayoutUpdateParam: LayoutParams = params!!
                var x = 0.0
                var y = 0.0
                var px = 0.0
                var py = 0.0
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            x = floatWindowLayoutUpdateParam.x.toDouble()
                            y = floatWindowLayoutUpdateParam.y.toDouble()
                            px = event.rawX.toDouble()
                            py = event.rawY.toDouble()
                        }
                        MotionEvent.ACTION_MOVE -> {
                            floatWindowLayoutUpdateParam.x = (x + event.rawX - px).toInt()
                            floatWindowLayoutUpdateParam.y = (y + event.rawY - py).toInt()
                            windowManager?.updateViewLayout(
                                overlayView,
                                floatWindowLayoutUpdateParam
                            )
                        }
                    }
                    return false
                }
            }
        )

        canReader.state.onEach { canState ->
            handler?.post {
                val text = overlayView?.findViewById<TextView>(R.id.info)
                text?.text = "${canState.data}\n${canState.status}"
            }
        }.launchIn(scope)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == START_SERVICE) {
            generateForegroundNotification()
            createOverlayWindow()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
        superJob.complete()
        if (overlayView?.isAttachedToWindow == true) {
            windowManager?.removeView(overlayView)
        }
    }

    private fun createOverlayWindow() {
        if (overlayView?.isAttachedToWindow == false) {
            windowManager?.addView(overlayView, params)
            val text = overlayView?.findViewById<TextView>(R.id.info)
            text?.text = "${canReader.state.value.data}\n${canReader.state.value.status}"
        }
    }

    private fun generateForegroundNotification() {
        val notification = UbNotify.create(
            context = this,
            icon = R.mipmap.ic_launcher_foreground,
            title = getString(R.string.service_title),
            message = getString(R.string.service_description)
        ).setChannelParams(
            id = getString(R.string.service_channel_id),
            name = getString(R.string.service_tracking_channel_name),
            channelParams = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    importance = NotificationManager.IMPORTANCE_LOW
                }
            }
        ).setParams {
            val appIntent =
                Intent(this@OverflowWindowService, MainActivity::class.java).let { appIntent ->
                    PendingIntent.getActivity(
                        this@OverflowWindowService,
                        0,
                        appIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                }
            this.setContentIntent(appIntent)
        }
            .build()

        startForeground(FOREGROUND_NOTIFICATION_ID, notification)
    }

    companion object {
        const val START_SERVICE = "${BuildConfig.APPLICATION_ID}/can/start"
        private const val TAG = "OverflowWindowService"
        private const val FOREGROUND_NOTIFICATION_ID = 42
        private const val LayoutParamFlags = LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
            LayoutParams.FLAG_NOT_TOUCH_MODAL or
            LayoutParams.FLAG_NOT_FOCUSABLE or
            LayoutParams.FLAG_DISMISS_KEYGUARD or
            LayoutParams.FLAG_SHOW_WHEN_LOCKED
    }
}