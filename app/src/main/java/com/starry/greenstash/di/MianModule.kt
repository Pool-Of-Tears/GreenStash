package com.starry.greenstash.di

import android.content.Context
import com.starry.greenstash.database.AppDatabase
import com.starry.greenstash.other.WelcomeDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class MianModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) = AppDatabase.getInstance(context)

    @Provides
    fun provideGoalDao(appDatabase: AppDatabase) = appDatabase.getGoalDao()

    @Provides
    fun provideTransactionDao(appDatabase: AppDatabase) = appDatabase.getTransactionDao()

    @Provides
    @Singleton
    fun provideDataStoreRepository(
        @ApplicationContext context: Context
    ) = WelcomeDataStore(context = context)

}