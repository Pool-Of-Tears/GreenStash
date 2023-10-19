/**
 * MIT License
 *
 * Copyright (c) [2022 - Present] Stɑrry Shivɑm
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.starry.greenstash.di

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import com.starry.greenstash.backup.BackupManager
import com.starry.greenstash.database.core.AppDatabase
import com.starry.greenstash.database.goal.GoalDao
import com.starry.greenstash.other.WelcomeDataStore
import com.starry.greenstash.reminder.ReminderManager
import com.starry.greenstash.reminder.ReminderNotificationSender
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterial3Api
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
    fun provideWidgetDao(appDatabase: AppDatabase) = appDatabase.getWidgetDao()

    @Provides
    @Singleton
    fun provideDataStoreRepository(
        @ApplicationContext context: Context
    ) = WelcomeDataStore(context = context)

    @Provides
    @Singleton
    fun provideReminderManager(@ApplicationContext context: Context) = ReminderManager(context)

    @Provides
    @Singleton
    fun provideReminderNotificationSender(@ApplicationContext context: Context) =
        ReminderNotificationSender(context)

    @Provides
    @Singleton
    fun providebackupmanager(@ApplicationContext context: Context, goalDao: GoalDao) =
        BackupManager(context = context, goalDao = goalDao)
}