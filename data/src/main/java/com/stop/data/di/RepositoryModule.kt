package com.stop.data.di

import com.stop.data.repository.*
import com.stop.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {

    @Binds
    @Singleton
    fun provideRemoteRepository(
        routeRepositoryImpl: RouteRepositoryImpl
    ): RouteRepository

    @Binds
    @Singleton
    fun provideNearPlaceRepository(
        nearPlaceRepositoryImpl: NearPlaceRepositoryImpl
    ): NearPlaceRepository


    @Binds
    @Singleton
    fun provideAlarmRepository(
        alarmRepositoryImpl: AlarmRepositoryImpl
    ): AlarmRepository

    @Binds
    @Singleton
    fun provideNowLocationRepository(
        nowLocationRepositoryImpl: NowLocationRepositoryImpl
    ): NowLocationRepository

    @Binds
    @Singleton
    fun provideRecentPlaceSearchRepository(
        recentPlaceSearchRepositoryImpl: RecentPlaceSearchRepositoryImpl
    ): RecentPlaceSearchRepository

}