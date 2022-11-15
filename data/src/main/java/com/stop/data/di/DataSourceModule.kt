package com.stop.data.di

import com.stop.data.remote.source.route.RouteRemoteDataSource
import com.stop.data.remote.source.route.RouteRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
internal interface DataSourceModule {

    @Binds
    fun provideRouteRemoteDataSource(
        routeRemoteDataSourceImpl: RouteRemoteDataSourceImpl
    ): RouteRemoteDataSource
}