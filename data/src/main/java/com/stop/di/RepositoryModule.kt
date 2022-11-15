package com.stop.di

import com.stop.repository.NearPlaceRepository
import com.stop.repository.NearPlaceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideNearPlaceRepository(nearPlaceRepositoryImpl: NearPlaceRepositoryImpl): NearPlaceRepository

}