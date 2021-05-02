package com.liviolopez.citycrimes.di

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.liviolopez.citycrimes.base.Constants
import com.liviolopez.citycrimes.base.DebugConfig
import com.liviolopez.citycrimes.data.local.AppDataBase
import com.liviolopez.citycrimes.data.local.AppDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalStoreModule {

    @Singleton
    @Provides
    fun provideDataBase(app: Application): AppDataBase {
        val builder =  Room.databaseBuilder(app, AppDataBase::class.java, Constants.DATABASE_NAME)
            .fallbackToDestructiveMigration()

        if(DebugConfig.Room.showQueries) {
            builder.setQueryCallback(
                { query, args -> Log.v("DB-LOG", "Query: $query ⟹ Args: $args") },
                Executors.newSingleThreadExecutor()
            )
        }

        return builder.build()
    }

    @Singleton
    @Provides
    fun provideDataStore(app: Application) = AppDataStore(app)
}
