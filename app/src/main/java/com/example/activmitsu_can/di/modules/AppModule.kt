package com.example.activmitsu_can.di.modules

import com.example.activmitsu_can.domain.can.CanReaderMock
import com.example.activmitsu_can.domain.can.ICanCommon
import com.example.activmitsu_can.domain.can.ICanReader
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun provideCanReader(reader: CanReaderMock): ICanReader

    @Binds
    @Singleton
    abstract fun provideCanCommon(reader: CanReaderMock): ICanCommon
}