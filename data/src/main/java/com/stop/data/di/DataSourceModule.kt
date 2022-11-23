package com.stop.data.di

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
    fun provideRouteRemoteDataSource(
        routeRemoteDataSourceImpl: RouteRemoteDataSourceImpl
    ): RouteRemoteDataSource

    @Binds
    @Singleton
    fun provideNearPlaceDataSource(
        nearPlaceRemoteDataSourceImpl: NearPlaceRemoteDataSourceImpl
    ) : NearPlaceRemoteDataSource

}