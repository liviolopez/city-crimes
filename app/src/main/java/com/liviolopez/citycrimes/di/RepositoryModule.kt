package com.liviolopez.citycrimes.di

import com.liviolopez.citycrimes.data.local.AppDataBase
import com.liviolopez.citycrimes.data.local.AppDataStore
import com.liviolopez.citycrimes.data.remote.RemoteDataSource
import com.liviolopez.citycrimes.repository.Repository
import com.liviolopez.citycrimes.repository.RepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(
        remoteData: RemoteDataSource,
        localData: AppDataBase,
        appDataStore: AppDataStore
    ): Repository {
        return RepositoryImpl(remoteData, localData, appDataStore)
    }

}