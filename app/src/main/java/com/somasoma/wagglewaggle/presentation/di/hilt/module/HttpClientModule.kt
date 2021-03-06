package com.somasoma.wagglewaggle.presentation.di.hilt.module

import com.somasoma.wagglewaggle.BuildConfig
import com.somasoma.wagglewaggle.presentation.PreferenceConstant
import com.somasoma.wagglewaggle.presentation.SharedPreferenceHelper
import com.somasoma.wagglewaggle.presentation.di.hilt.qualifier.ForAccessHttp
import com.somasoma.wagglewaggle.presentation.di.hilt.qualifier.ForPublicHttp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HttpClientModule {
    companion object {
        const val TimeOutInSeconds = 15L
    }

    @Provides
    @Singleton
    @ForAccessHttp
    fun provideOkHttpClientForAccessToken(sharedPreferenceHelper: SharedPreferenceHelper): OkHttpClient {
        val builder = OkHttpClient.Builder()

        // add authorization header
        builder.addInterceptor(Interceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .addHeader(
                        "Authorization", "Bearer ${
                            sharedPreferenceHelper.getString(PreferenceConstant.ACCESS_TOKEN)
                        }"
                    )
                    .build()
            )
        })

        // add logging
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addInterceptor(loggingInterceptor)
        }

        builder.readTimeout(TimeOutInSeconds, TimeUnit.SECONDS)
        builder.writeTimeout(TimeOutInSeconds, TimeUnit.SECONDS)
        builder.connectTimeout(TimeOutInSeconds, TimeUnit.SECONDS)

        return builder.build()
    }

    @Provides
    @Singleton
    @ForPublicHttp
    fun provideOkHttpClientForPublic(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        // add logging
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addInterceptor(loggingInterceptor)
        }

        builder.readTimeout(TimeOutInSeconds, TimeUnit.SECONDS)
        builder.writeTimeout(TimeOutInSeconds, TimeUnit.SECONDS)
        builder.connectTimeout(TimeOutInSeconds, TimeUnit.SECONDS)

        return builder.build()
    }
}