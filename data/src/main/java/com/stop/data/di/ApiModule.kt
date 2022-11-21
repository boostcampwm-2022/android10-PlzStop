package com.stop.data.di

import com.stop.data.remote.network.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object ApiModule {

    @Provides
    fun provideTmapApiService(retrofit: Retrofit): TmapApiService {
        return retrofit.create(TmapApiService::class.java)
    }

    @Provides
    fun provideFakeTmapApiService(retrofit: Retrofit): FakeTmapApiService {
        return retrofit.create(FakeTmapApiService::class.java)
    }

    @Provides
    fun provideOpenApiSeoulService(retrofit: Retrofit): OpenApiSeoulService {
        return retrofit.create(OpenApiSeoulService::class.java)
    }

    @Provides
    fun provideWsBusApiService(retrofit: Retrofit): WsBusApiService {
        return retrofit.create(WsBusApiService::class.java)
    }

    @Provides
    fun provideApisDataService(retrofit: Retrofit): ApisDataService {
        return retrofit.create(ApisDataService::class.java)
    }

    @Provides
    @Singleton
    fun providePlaceApiService(retrofit: Retrofit): NearPlaceApiService {
        return retrofit.create(NearPlaceApiService::class.java)
    }
}