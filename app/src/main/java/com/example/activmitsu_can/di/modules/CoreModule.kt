package com.example.activmitsu_can.di.modules

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import javax.inject.Named
import javax.inject.Singleton

@Module
object CoreModule {

    @DelicateCoroutinesApi
    @Provides
    @Singleton
    @Named(value = "globalScope")
    fun providesApplicationCoroutineContext(): CoroutineScope = GlobalScope
}