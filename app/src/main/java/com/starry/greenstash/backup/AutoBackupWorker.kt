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


package com.starry.greenstash.backup

import android.content.Context
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.starry.greenstash.utils.PreferenceUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AutoBackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val backupManager: BackupManager,
    private val preferenceUtil: PreferenceUtil
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): ListenableWorker.Result {
        val isAutoBackupEnabled = preferenceUtil.getBoolean(PreferenceUtil.AUTO_BACKUP_BOOL, false)
        val directoryUriStr = preferenceUtil.getString(PreferenceUtil.AUTO_BACKUP_DIRECTORY_URI_STR, "")

        if (!isAutoBackupEnabled || directoryUriStr.isNullOrEmpty()) {
            return ListenableWorker.Result.success()
        }

        val directoryUri = directoryUriStr.toUri()
        val success = backupManager.performAutomaticBackup(directoryUri)

        return if (success) {
            preferenceUtil.putLong(PreferenceUtil.AUTO_BACKUP_LAST_TIME_MS_LONG, System.currentTimeMillis())
            ListenableWorker.Result.success()
        } else {
            ListenableWorker.Result.retry()
        }
    }
}