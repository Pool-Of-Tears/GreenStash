package com.starry.greenstash.ui.screens.backups

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starry.greenstash.backup.BackupManager
import com.starry.greenstash.backup.BackupType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupManager: BackupManager
) : ViewModel() {

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
}