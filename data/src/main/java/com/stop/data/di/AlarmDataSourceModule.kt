package com.stop.data.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.stop.data.local.source.alarm.AlarmLocalDataSource
import com.stop.data.local.source.alarm.AlarmLocalDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlarmDataSourceModule {

    @Provides
    @Singleton
    fun provideAlarmLocalDataSource(
        @ApplicationContext context: Context,
        moshi: Moshi
    ): AlarmLocalDataSource {
        return AlarmLocalDataSourceImpl(
            context,
            moshi
        )
    }

}