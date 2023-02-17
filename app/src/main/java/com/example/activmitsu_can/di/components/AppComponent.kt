package com.example.activmitsu_can.di.components

import android.content.Context
import com.example.activmitsu_can.di.MitsuViewModelFactory
import com.example.activmitsu_can.di.modules.AppModule
import com.example.activmitsu_can.di.modules.CoreModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [CoreModule::class, AppModule::class]
)
@Singleton
interface AppComponent {

    val viewModelProvider: MitsuViewModelFactory

    val context: Context

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }
}