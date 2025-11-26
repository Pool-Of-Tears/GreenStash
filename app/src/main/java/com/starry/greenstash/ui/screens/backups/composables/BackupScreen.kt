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


package com.starry.greenstash.ui.screens.backups.composables

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.starry.greenstash.R
import com.starry.greenstash.backup.BackupType
import com.starry.greenstash.ui.common.TipCardNoDismiss
import com.starry.greenstash.ui.screens.backups.BackupViewModel
import com.starry.greenstash.ui.theme.greenstashFont
import com.starry.greenstash.utils.weakHapticFeedback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.StandardCharsets


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(navController: NavController) {
    val view = LocalView.current
    val context = LocalContext.current
    val viewModel = hiltViewModel<BackupViewModel>()

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.backup_screen_header),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = greenstashFont
                    )
                }, navigationIcon = {
                    IconButton(onClick = {
                        view.weakHapticFeedback()
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }, scrollBehavior = scrollBehavior, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                )
            )
        }, content = {
            val ftpButtonText = remember { mutableStateOf("") }
            val selectedBackupType = remember { mutableStateOf(BackupType.JSON) }
            val showFileTypePicker = remember { mutableStateOf(false) }

            val backupRestoreLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                    uri?.let { fileUri ->
                        context.contentResolver.openInputStream(fileUri)?.let { ips ->
                            // read json content from input stream
                            val bufferSize = 1024
                            val buffer = CharArray(bufferSize)
                            val out = StringBuilder()
                            val reader: Reader = InputStreamReader(ips, StandardCharsets.UTF_8)
                            var numRead: Int
                            while (reader.read(buffer, 0, buffer.size)
                                    .also { nRead -> numRead = nRead } > 0
                            ) {
                                out.appendRange(buffer, 0, numRead)
                            }

                            viewModel.restoreBackup(
                                backupType = selectedBackupType.value,
                                backupString = out.toString(),
                                onSuccess = {
                                    coroutineScope.launch {
                                        snackBarHostState.showSnackbar(context.getString(R.string.backup_restore_success))
                                    }
                                },
                                onFailure = {
                                    coroutineScope.launch {
                                        snackBarHostState.showSnackbar(context.getString(R.string.unknown_error))
                                    }
                                }
                            )
                        }
                    }

                }


            if (showFileTypePicker.value) {
                BackupFileTypePicker(
                    showFileTypePicker = showFileTypePicker,
                    buttonText = ftpButtonText.value,
                    onConfirm = { backupType ->
                        // Used for restoring backup inside file picker launcher.
                        selectedBackupType.value = backupType

                        if (ftpButtonText.value.isEmpty()) {
                            return@BackupFileTypePicker
                        }

                        if (ftpButtonText.value == context.getString(R.string.backup_ftp_create_button)) {
                            viewModel.takeBackup(backupType) { intent ->
                                context.startActivity(intent)
                            }
                        } else {
                            // Restore backup
                            when (backupType) {
                                BackupType.JSON -> {
                                    backupRestoreLauncher.launch(arrayOf("application/json"))
                                }

                                BackupType.CSV -> {
                                    backupRestoreLauncher.launch(
                                        arrayOf(
                                            "text/csv",
                                            "text/comma-separated-values",
                                            "application/vnd.ms-excel"
                                        )
                                    )
                                }
                            }
                        }
                    }
                )
            }

            BackupScreenContent(
                paddingValues = it,
                onBackupClicked = {
                    ftpButtonText.value =
                        context.getString(R.string.backup_ftp_create_button)
                    showFileTypePicker.value = true
                },
                onRestoreClicked = {
                    ftpButtonText.value =
                        context.getString(R.string.backup_ftp_restore_button)
                    showFileTypePicker.value = true
                }
            )
        })
}

@Composable
private fun BackupScreenContent(
    paddingValues: PaddingValues, onBackupClicked: () -> Unit, onRestoreClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    5.dp
                )
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = R.drawable.backup_icon,
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
        ) {
            Text(
                text = stringResource(id = R.string.backup_screen_text),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                fontFamily = greenstashFont
            )
            Text(
                text = stringResource(id = R.string.backup_screen_sub_text),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 12.dp),
                fontFamily = greenstashFont
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            FilledTonalButton(
                onClick = onBackupClicked,
                modifier = Modifier
                    .padding(start = 14.dp)
                    .weight(0.45f),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.backup_button),
                    fontFamily = greenstashFont
                )
            }

            Spacer(modifier = Modifier.weight(0.04f))

            OutlinedButton(
                onClick = onRestoreClicked,
                modifier = Modifier
                    .padding(end = 14.dp)
                    .weight(0.45f),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.backup_restore_button),
                    fontFamily = greenstashFont
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BackupFileTypePicker(
    showFileTypePicker: MutableState<Boolean>,
    buttonText: String,
    onConfirm: (BackupType) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            coroutineScope.launch {
                sheetState.hide()
                delay(300)
                showFileTypePicker.value = false
            }
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            val (selectedBackupType, onBackupTypeSelected) = remember {
                mutableStateOf(BackupType.JSON)
            }

            Text(
                text = stringResource(id = R.string.backup_select_file_type),
                fontFamily = greenstashFont,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp)
            ) {
                SegmentedButton(
                    selected = selectedBackupType == BackupType.JSON,
                    onClick = { onBackupTypeSelected(BackupType.JSON) },
                    shape = RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp),
                    label = {
                        Text(
                            text = BackupType.JSON.name, fontFamily = greenstashFont
                        )
                    },
                    icon = {
                        if (selectedBackupType == BackupType.JSON) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    colors = SegmentedButtonDefaults.colors(
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                    )
                )

                SegmentedButton(
                    selected = selectedBackupType == BackupType.CSV,
                    onClick = { onBackupTypeSelected(BackupType.CSV) },
                    shape = RoundedCornerShape(topEnd = 14.dp, bottomEnd = 14.dp),
                    label = {
                        Text(
                            text = BackupType.CSV.name,
                            fontFamily = greenstashFont
                        )
                    },
                    icon = {
                        if (selectedBackupType == BackupType.CSV) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    colors = SegmentedButtonDefaults.colors(
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                    )
                )
            }


            Spacer(modifier = Modifier.height(4.dp))

            TipCardNoDismiss(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                icon = ImageVector.vectorResource(id = R.drawable.ic_backup_csv),
                description = stringResource(id = R.string.backup_csv_note),
                showTipCard = selectedBackupType == BackupType.CSV,
            )

            Button(
                onClick = {
                    onConfirm(selectedBackupType)
                    coroutineScope.launch {
                        sheetState.hide()
                        delay(300)
                        showFileTypePicker.value = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(
                    text = buttonText,
                    fontFamily = greenstashFont
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

