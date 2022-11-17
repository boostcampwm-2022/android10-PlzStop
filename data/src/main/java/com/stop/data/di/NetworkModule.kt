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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
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

    private const val TRANSPORT_URI = "transit/routes"
    private const val FAKE_JSON_URI = "response.json"

    @Provides
    fun provideOkHttpClient(
//        tmapInterceptor: TmapInterceptor,
        fakeTmapInterceptor: FakeTmapInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(fakeTmapInterceptor)
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
    fun provideFakeTmapInterceptor(): FakeTmapInterceptor {
        return FakeTmapInterceptor()
    }

    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
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

    class FakeTmapInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            if (chain.request().url.toUri().toString().contains(TRANSPORT_URI)) {
                val response = readJson(FAKE_JSON_URI)
                return chain.proceed(chain.request())
                    .newBuilder()
                    .code(200)
                    .protocol(Protocol.HTTP_2)
                    .message("success")
                    .body(
                        response.toByteArray()
                            .toResponseBody("application/json".toMediaTypeOrNull())
                    ).addHeader("content-type", "application/json")
                    .build()
            }
            return chain.proceed(chain.request())
        }

        private fun readJson(fileName: String): String {
            return Thread.currentThread().contextClassLoader?.getResource(fileName)
                ?.readText() ?: ""
        }
    }
}