package com.stop.domain.di

import com.stop.domain.usecase.GetRouteUseCase
import com.stop.domain.usecase.GetRouteUseCaseImpl
import com.stop.domain.usecase.geoLocation.GeoLocationUseCase
import com.stop.domain.usecase.geoLocation.GeoLocationUseCaseImpl
import com.stop.domain.usecase.nearplace.GetNearPlacesUseCase
import com.stop.domain.usecase.nearplace.GetNearPlacesUseCaseImpl
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

    @Binds
    @Singleton
    fun provideGeoLocationUseCase(geoLocationUseCaseImpl: GeoLocationUseCaseImpl): GeoLocationUseCase


    @Binds
    @Singleton
    abstract fun provideGetNearPlaceUseCase(getNearPlacesUseCaseImpl: GetNearPlacesUseCaseImpl): GetNearPlacesUseCase
}