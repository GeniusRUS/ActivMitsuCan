package com.example.activmitsu_can.di.modules

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
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

    @Singleton
    @Provides
    fun provideDataStore(context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = {
                context.preferencesDataStoreFile("laundry")
            }
        )
}