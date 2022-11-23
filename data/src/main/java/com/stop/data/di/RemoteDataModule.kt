package com.stop.data.di

import com.stop.data.remote.source.nearplace.NearPlaceRemoteDataSource
import com.stop.data.remote.source.nearplace.NearPlaceRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RemoteDataModule {

    @Binds
    @Singleton
    abstract fun provideNearPlaceRemoteData(
        nearPlaceRemoteDataSourceImpl: NearPlaceRemoteDataSourceImpl
    ): NearPlaceRemoteDataSource
}