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


package com.starry.greenstash.ui.screens.backups

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.starry.greenstash.backup.AutoBackupWorker
import com.starry.greenstash.backup.BackupManager
import com.starry.greenstash.backup.BackupType
import com.starry.greenstash.utils.PreferenceUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupManager: BackupManager,
    private val preferenceUtil: PreferenceUtil,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    // Auto backup preferences
    private val _autoBackup = MutableLiveData(false)
    private val _autoBackupDirectory = MutableLiveData("")
    private val _autoBackupInterval = MutableLiveData(1)
    private val _autoBackupMaxKeep = MutableLiveData(5)
    private val _lastBackupTime = MutableLiveData(0L)

    val autoBackup: LiveData<Boolean> = _autoBackup
    val autoBackupDirectory: LiveData<String> = _autoBackupDirectory
    val autoBackupInterval: LiveData<Int> = _autoBackupInterval
    val autoBackupMaxKeep: LiveData<Int> = _autoBackupMaxKeep
    val lastBackupTime: LiveData<Long> = _lastBackupTime

    companion object {
        const val AUTO_BACKUP_WORK_NAME = "auto_backup_work"
    }

    init {
        _autoBackup.value = preferenceUtil.getBoolean(PreferenceUtil.AUTO_BACKUP_BOOL, false)
        _autoBackupDirectory.value = preferenceUtil.getString(PreferenceUtil.AUTO_BACKUP_DIRECTORY_URI_STR, "")
        _autoBackupInterval.value = preferenceUtil.getInt(PreferenceUtil.AUTO_BACKUP_INTERVAL_DAYS_INT, 1)
        _autoBackupMaxKeep.value = preferenceUtil.getInt(PreferenceUtil.AUTO_BACKUP_MAX_KEEP_INT, 5)
        _lastBackupTime.value = preferenceUtil.getLong(PreferenceUtil.AUTO_BACKUP_LAST_TIME_MS_LONG, 0L)
    }

    fun takeBackup(backupType: BackupType, onComplete: (Intent) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val backupIntent = backupManager.createDatabaseBackup(backupType)
            withContext(Dispatchers.Main) { onComplete(backupIntent) }
        }
    }

    fun restoreBackup(
        backupType: BackupType,
        backupString: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            backupManager.restoreDatabaseBackup(
                backupType = backupType,
                backupString = backupString,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }

    fun setAutoBackup(enabled: Boolean, onDirectoryMissing: () -> Unit) {
        if (enabled && (_autoBackupDirectory.value.isNullOrEmpty())) {
            _autoBackup.value = false
            onDirectoryMissing()
            return
        }

        _autoBackup.value = enabled
        preferenceUtil.putBoolean(PreferenceUtil.AUTO_BACKUP_BOOL, enabled)
        if (enabled) {
            scheduleAutoBackup()
        } else {
            cancelAutoBackup()
        }
    }

    fun setAutoBackupDirectory(uri: String) {
        _autoBackupDirectory.postValue(uri)
        preferenceUtil.putString(PreferenceUtil.AUTO_BACKUP_DIRECTORY_URI_STR, uri)
        if (autoBackup.value == true) {
            scheduleAutoBackup()
        }
    }

    fun setAutoBackupInterval(days: Int) {
        _autoBackupInterval.value = days
        preferenceUtil.putInt(PreferenceUtil.AUTO_BACKUP_INTERVAL_DAYS_INT, days)
        if (autoBackup.value == true) {
            scheduleAutoBackup()
        }
    }

    fun setAutoBackupMaxKeep(maxKeep: Int) {
        _autoBackupMaxKeep.postValue(maxKeep)
        preferenceUtil.putInt(PreferenceUtil.AUTO_BACKUP_MAX_KEEP_INT, maxKeep)
    }

    private fun scheduleAutoBackup() {
        val days = autoBackupInterval.value ?: 1
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<AutoBackupWorker>(
            days.toLong(), TimeUnit.DAYS
        ).setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            AUTO_BACKUP_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    private fun cancelAutoBackup() {
        WorkManager.getInstance(context).cancelUniqueWork(AUTO_BACKUP_WORK_NAME)
    }
}