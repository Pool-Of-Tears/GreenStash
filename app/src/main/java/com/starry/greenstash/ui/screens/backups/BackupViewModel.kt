package com.starry.greenstash.ui.screens.backups

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starry.greenstash.backup.BackupManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupManager: BackupManager
) : ViewModel() {

    fun takeBackup(onComplete: (Intent) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val backupIntent = backupManager.createDatabaseBackup()
            withContext(Dispatchers.Main) { onComplete(backupIntent) }
        }
    }

    fun restoreBackup(jsonString: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            backupManager.restoreDatabaseBackup(
                jsonString = jsonString,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }
}