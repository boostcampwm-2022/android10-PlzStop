package com.stop.di

import android.content.Context
import androidx.work.WorkManager
import com.stop.AlarmFunctions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AlarmModule {

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ) = WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun provideAlarmFunctions(
        @ApplicationContext context: Context
    ) = AlarmFunctions(context)

}