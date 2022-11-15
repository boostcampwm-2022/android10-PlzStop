package com.stop.di

import com.stop.usecase.nearplace.GetNearPlaceListUseCase
import com.stop.usecase.nearplace.GetNearPlaceListUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal abstract class UseCaseModule {

    @Binds
    @Singleton
    abstract fun provideGetNearPlaceUseCase(getNearPlaceListUseCaseImpl: GetNearPlaceListUseCaseImpl): GetNearPlaceListUseCase

}