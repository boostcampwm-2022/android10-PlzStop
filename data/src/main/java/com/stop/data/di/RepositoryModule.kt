package com.stop.data.di

import com.stop.data.repository.RouteRepositoryImpl
import com.stop.domain.repository.RouteRepository
import com.stop.data.repository.NearPlaceRepositoryImpl
import com.stop.domain.repository.NearPlaceRepository
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

}