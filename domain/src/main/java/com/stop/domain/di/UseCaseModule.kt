package com.stop.domain.di

import com.stop.domain.usecase.geoLocation.GeoLocationUseCase
import com.stop.domain.usecase.geoLocation.GeoLocationUseCaseImpl
import com.stop.domain.usecase.nearplace.GetNearPlacesUseCase
import com.stop.domain.usecase.nearplace.GetNearPlacesUseCaseImpl
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
    abstract fun provideGetNearPlaceUseCase(getNearPlacesUseCaseImpl: GetNearPlacesUseCaseImpl): GetNearPlacesUseCase

    @Binds
    @Singleton
    abstract fun provideGeoLocationUseCase(geoLocationUseCaseImpl: GeoLocationUseCaseImpl): GeoLocationUseCase

}