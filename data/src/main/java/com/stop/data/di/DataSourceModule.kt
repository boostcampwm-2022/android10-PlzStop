package com.stop.data.di

import com.stop.data.remote.network.TmapApiService
import com.stop.data.remote.source.route.RouteRemoteDataSource
import com.stop.data.remote.source.route.RouteRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataSourceModule {

    @Provides
    @Singleton
    fun provideRouteRemoteDataSource(
        tmapApiService: TmapApiService
    ): RouteRemoteDataSource {
        return RouteRemoteDataSourceImpl(tmapApiService)
    }

}