package com.stop.di

import com.stop.remote.source.nearplace.NearPlaceRemoteDataSource
import com.stop.remote.source.nearplace.NearPlaceRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteDataModule {

    @Binds
    @Singleton
    abstract fun provideNearPlaceRemoteData(nearPlaceRemoteDataSourceImpl: NearPlaceRemoteDataSourceImpl): NearPlaceRemoteDataSource

}