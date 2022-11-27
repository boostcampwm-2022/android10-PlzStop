package com.stop.data.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

   /* @Provides
    @Singleton
    fun provideApplicationDatabase(@ApplicationContext context: Context): TroubleShooterApplicationDatabase {
        return Room.databaseBuilder(
            context,
            TroubleShooterApplicationDatabase::class.java,
            DB_NAME
        ).fallbackToDestructiveMigration()
            .build()
    }*/

}