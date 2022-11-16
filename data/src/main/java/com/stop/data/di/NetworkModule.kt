package com.stop.data.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.stop.data.BuildConfig
import com.stop.data.remote.adapter.route.ResultCallAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object NetworkModule {

    private const val T_MAP_APP_KEY_NAME = "appKey"
    private const val T_MAP_APP_KEY_VALUE = BuildConfig.T_MAP_APP_KEY
    private const val T_MAP_URL = "https://apis.openapi.sk.com/"

    @Provides
    fun provideOkHttpClient(tmapInterceptor: TmapInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(tmapInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideTmapInterceptor(): TmapInterceptor {
        return TmapInterceptor()
    }

    @Provides
    fun provideResultCallAdapter(): ResultCallAdapter.Factory {
        return ResultCallAdapter.Factory()
    }

    @Provides
    @Singleton
    fun provideRetrofitInstance(
        okHttpClient: OkHttpClient,
        moshi: Moshi,
        resultCallAdapter: ResultCallAdapter.Factory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(T_MAP_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(resultCallAdapter)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    class TmapInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            return with(chain) {
                val newRequest = request().newBuilder()
                    .addHeader(T_MAP_APP_KEY_NAME, T_MAP_APP_KEY_VALUE)
                    .build()
                proceed(newRequest)
            }
        }
    }
}