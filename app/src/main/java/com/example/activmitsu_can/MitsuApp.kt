package com.example.activmitsu_can

import android.app.Application
import com.example.activmitsu_can.di.DIManager
import com.example.activmitsu_can.di.components.DaggerAppComponent

class MitsuApp : Application() {

    override fun onCreate() {
        super.onCreate()

        DIManager.appComponent = DaggerAppComponent.builder()
            .context(this)
            .build()
    }
}