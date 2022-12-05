package com.stop.data.di

import android.content.Context
import androidx.room.Room
import com.stop.data.local.database.TroubleShooterApplicationDatabase
import com.stop.data.local.database.TroubleShooterApplicationDatabase.Companion.DB_NAME
import com.stop.data.local.database.dao.RecentPlaceSearchDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideApplicationDatabase(@ApplicationContext context: Context): TroubleShooterApplicationDatabase {
        return Room.databaseBuilder(
            context,
            TroubleShooterApplicationDatabase::class.java,
            DB_NAME
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideRecentPlaceSearchDao(
        troubleShooterApplicationDatabase: TroubleShooterApplicationDatabase
    ): RecentPlaceSearchDao {
        return troubleShooterApplicationDatabase.getRecentPlaceSearchDao()
    }

}