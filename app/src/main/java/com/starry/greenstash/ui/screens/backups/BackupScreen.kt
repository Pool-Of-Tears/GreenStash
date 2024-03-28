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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.starry.greenstash.R
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.StandardCharsets


@ExperimentalMaterial3Api
@Composable
fun BackupScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<BackupViewModel>()

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.backup_screen_header),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }, navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }, scrollBehavior = scrollBehavior, colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                )
            )
        }, content = {
            val backupLauncher =
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

                            viewModel.restoreBackup(jsonString = out.toString(),
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

            BackupScreenContent(paddingValues = it,
                onBackupClicked = { viewModel.takeBackup { intent -> context.startActivity(intent) } },
                onRestoreClicked = { backupLauncher.launch(arrayOf("application/json")) }
            )
        })
}

@Composable
fun BackupScreenContent(
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
                    model = R.drawable.backup_logo,
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
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
            )
            Text(
                text = stringResource(id = R.string.backup_screen_sub_text),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 12.dp)
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
                Text(text = stringResource(id = R.string.backup_button))
            }

            Spacer(modifier = Modifier.weight(0.04f))

            OutlinedButton(
                onClick = onRestoreClicked,
                modifier = Modifier
                    .padding(end = 14.dp)
                    .weight(0.45f),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(text = stringResource(id = R.string.restore_button))
            }
        }
    }
}