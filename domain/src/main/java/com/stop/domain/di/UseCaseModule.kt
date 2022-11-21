package com.stop.domain.di

import com.stop.domain.usecase.GetRouteUseCase
import com.stop.domain.usecase.GetRouteUseCaseImpl
import dagger.Module
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface UseCaseModule {

    @Binds
    @Singleton
    fun provideGetRouteUseCase(getRouteUseCaseImpl: GetRouteUseCaseImpl): GetRouteUseCase
}