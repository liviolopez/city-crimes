package com.liviolopez.citycrimes.di

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.liviolopez.citycrimes.base.Constants
import com.liviolopez.citycrimes.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RemoteModule {

    @Singleton
    @Provides
    fun provideOkHttpClient() = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    @Singleton
    @Provides
    fun provideApiService(okHttpClient: OkHttpClient, baseUrl: String): ApiService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create()
                )
            )
            .build()
            .create(ApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RemoteParamsModule {

    @Singleton
    @Provides
    fun provideBaseUrl(): String {
        return Constants.BASE_URL
    }

}