package com.stop.data.di

import com.stop.data.remote.network.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object ApiModule {

    @Provides
    @Singleton
    fun provideTmapApiService(@Named("Tmap") retrofit: Retrofit): TmapApiService {
        return retrofit.create(TmapApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFakeTmapApiService(@Named("Tmap") retrofit: Retrofit): FakeTmapApiService {
        return retrofit.create(FakeTmapApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOpenApiSeoulService(@Named("OpenApiSeoul") retrofit: Retrofit): OpenApiSeoulService {
        return retrofit.create(OpenApiSeoulService::class.java)
    }

    @Provides
    @Singleton
    fun provideWsBusApiService(@Named("WsBus") retrofit: Retrofit): WsBusApiService {
        return retrofit.create(WsBusApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideApisDataService(@Named("ApisData") retrofit: Retrofit): ApisDataService {
        return retrofit.create(ApisDataService::class.java)
    }
}