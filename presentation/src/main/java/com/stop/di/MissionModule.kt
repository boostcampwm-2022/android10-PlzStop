package com.stop.di

import com.stop.ui.mission.MissionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object MissionModule {

    @Provides
    @Singleton
    fun provideMissionManager() = MissionManager()

}