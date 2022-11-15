package com.stop.data.di

import com.stop.data.remote.network.TmapApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@InstallIn(SingletonComponent::class)
@Module
object ApiModule {

    @Provides
    fun provideTmapApiService(retrofit: Retrofit): TmapApiService {
        return retrofit.create(TmapApiService::class.java)
    }
}