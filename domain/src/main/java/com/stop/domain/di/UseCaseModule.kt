package com.stop.domain.di

import com.stop.domain.usecase.alarm.*
import com.stop.domain.usecase.route.GetRouteUseCase
import com.stop.domain.usecase.route.GetRouteUseCaseImpl
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
    fun provideGetNearPlaceUseCase(getNearPlacesUseCaseImpl: GetNearPlacesUseCaseImpl): GetNearPlacesUseCase

    @Binds
    @Singleton
    fun provideGetAlarmUseCase(getAlarmUseCaseImpl: GetAlarmUseCaseImpl): GetAlarmUseCase

    @Binds
    @Singleton
    fun provideInsertAlarmUseCase(insertAlarmUseCaseImpl: InsertAlarmUseCaseImpl): InsertAlarmUseCase

    @Binds
    @Singleton
    fun provideDeleteAlarmUseCase(deleteAlarmUseCaseImpl: DeleteAlarmUseCaseImpl): DeleteAlarmUseCase

}