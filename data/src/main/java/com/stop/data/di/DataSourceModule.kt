package com.stop.data.di

import com.stop.data.local.source.alarm.RecentPlaceSearchLocalDataSource
import com.stop.data.local.source.alarm.RecentPlaceSearchLocalDataSourceImpl
import com.stop.data.remote.source.nearplace.NearPlaceRemoteDataSource
import com.stop.data.remote.source.nearplace.NearPlaceRemoteDataSourceImpl
import com.stop.data.remote.source.nowlocation.NowLocationRemoteDataSource
import com.stop.data.remote.source.nowlocation.NowLocationRemoteDataSourceImpl
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
    fun provideNowLocationRemoteDataSource(
        nowLocationRemoteDataSourceImpl: NowLocationRemoteDataSourceImpl
    ): NowLocationRemoteDataSource

    @Binds
    @Singleton
    fun provideRecentPlaceSearchLocalDataSource(
        recentPlaceSearchLocalDataSourceImpl: RecentPlaceSearchLocalDataSourceImpl
    ) : RecentPlaceSearchLocalDataSource

}