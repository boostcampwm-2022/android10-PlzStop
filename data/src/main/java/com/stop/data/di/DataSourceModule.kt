package com.stop.data.di

import com.stop.data.local.source.alarm.AlarmLocalDataSource
import com.stop.data.local.source.alarm.AlarmLocalDataSourceImpl
import com.stop.data.remote.source.nearplace.NearPlaceRemoteDataSource
import com.stop.data.remote.source.nearplace.NearPlaceRemoteDataSourceImpl
import com.stop.data.remote.source.route.RouteRemoteDataSource
import com.stop.data.remote.source.route.RouteRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal interface DataSourceModule {

    @Binds
    @Singleton
    fun provideNearPlaceRemoteDataSource(
        nearPlaceRemoteDataSourceImpl: NearPlaceRemoteDataSourceImpl
    ): NearPlaceRemoteDataSource

    @Binds
    @Singleton
    fun provideRouteRemoteDataSource(
        routeRemoteDataSourceImpl: RouteRemoteDataSourceImpl
    ): RouteRemoteDataSource

    @Binds
    @Singleton
    fun provideAlarmLocalDataSource(
        alarmLocalDataSourceImpl: AlarmLocalDataSourceImpl
    ): AlarmLocalDataSource

}