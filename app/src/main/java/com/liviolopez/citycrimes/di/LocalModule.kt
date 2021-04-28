package com.liviolopez.citycrimes.di

import android.app.Application
import androidx.room.Room
import com.liviolopez.citycrimes.base.Constants
import com.liviolopez.citycrimes.data.local.AppDataBase
import com.liviolopez.citycrimes.data.local.AppDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalStoreModule {

    @Singleton
    @Provides
    fun provideDataBase(app: Application): AppDataBase =
        Room.databaseBuilder(app, AppDataBase::class.java, Constants.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideDataStore(app: Application) = AppDataStore(app)
}